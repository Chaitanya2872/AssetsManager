package com.bmsedge.asset.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

/**
 * Requires an identity-service access token on every {@code /api/**} request, verified with
 * {@code app.security.jwt.public-key} - the same RS256 key the gateway and every other service verify with.
 *
 * <p>The gateway already checks the token on its route, but this service also publishes its own port, and a
 * request sent straight to that port skipped every check. Callers inside the platform (helpdesk-service)
 * forward the user's token, so the same check applies to them.
 */
@Component
public class IdentityTokenFilter extends OncePerRequestFilter {

    /** Reachable without a token: the liveness probe only reports a count. */
    private static final Set<String> PUBLIC_PATHS = Set.of("/api/assets/health");

    private final boolean enabled;
    private final PublicKey publicKey;

    public IdentityTokenFilter(@Value("${app.security.enabled:true}") boolean enabled,
                               @Value("${app.security.jwt.public-key:}") String configuredKey) {
        this.enabled = enabled;
        this.publicKey = parsePublicKey(configuredKey);
        if (enabled && publicKey == null) {
            throw new IllegalStateException(
                    "app.security.jwt.public-key must be set while app.security.enabled is true");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !enabled
                || !path.startsWith("/api/")
                || PUBLIC_PATHS.contains(path)
                // CORS preflights never carry credentials.
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ") || !isValid(authorization.substring(7))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\","
                    + "\"message\":\"A valid access token is required\"}");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private static PublicKey parsePublicKey(String configured) {
        if (configured == null || configured.isBlank()) {
            return null;
        }
        try {
            byte[] der = Base64.getDecoder().decode(
                    configured.replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                            .replaceAll("[^A-Za-z0-9+/=]", ""));
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "app.security.jwt.public-key is not a valid X.509 RSA public key.", ex);
        }
    }
}

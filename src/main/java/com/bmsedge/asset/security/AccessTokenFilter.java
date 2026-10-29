package com.bmsedge.asset.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Requires a valid, unrevoked identity-service access token on every API call.
 *
 * <p>This service had no authentication of its own: the gateway's token check was the only one,
 * so a request reaching it directly - its published port, or anything else on the Docker network -
 * read and changed assets with no token at all, and a deactivated employee's token kept working
 * here until it expired. Now it verifies the RS256 signature with identity's public key, the
 * issuer and expiry, and identity's revocation feed, as every other service does. Authorization
 * stays as it was: any authenticated Verse user, which is what the gateway already enforced.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AccessTokenFilter extends OncePerRequestFilter {

  private final PublicKey verificationKey;
  private final String issuer;
  private final TokenRevocationRegistry revocationRegistry;

  public AccessTokenFilter(
      @Value("${app.security.jwt.public-key:${JWT_PUBLIC_KEY:}}") String publicKey,
      @Value("${app.security.jwt.issuer:${JWT_ISSUER:fawnix-verse}}") String issuer,
      TokenRevocationRegistry revocationRegistry
  ) {
    if (publicKey == null || publicKey.isBlank()) {
      throw new IllegalStateException("JWT_PUBLIC_KEY is required: asset management verifies identity tokens.");
    }
    this.verificationKey = parsePublicKey(publicKey);
    this.issuer = issuer;
    this.revocationRegistry = revocationRegistry;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // Health for Eureka and the container check; CORS preflight carries no token.
    return !request.getRequestURI().startsWith("/api/") || "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    Claims claims;
    try {
      claims = Jwts.parser()
          .verifyWith(verificationKey)
          .requireIssuer(issuer)
          .build()
          .parseSignedClaims(authorization.substring(7))
          .getPayload();
    } catch (Exception exception) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    Object sessionId = claims.get("sessionId");
    Instant issuedAt = claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant();
    if (claims.getSubject() == null
        || revocationRegistry.isRevoked(claims.getSubject(), issuedAt, sessionId == null ? null : String.valueOf(sessionId))) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    request.setAttribute("verse.userId", claims.getSubject());
    chain.doFilter(request, response);
  }

  private static PublicKey parsePublicKey(String configured) {
    try {
      byte[] der = Base64.getDecoder().decode(configured
          .replace("-----BEGIN PUBLIC KEY-----", "")
          .replace("-----END PUBLIC KEY-----", "")
          .replaceAll("[^A-Za-z0-9+/=]", ""));
      return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
    } catch (Exception exception) {
      throw new IllegalStateException("JWT_PUBLIC_KEY is not a valid X.509 RSA public key.", exception);
    }
  }
}

package com.bmsedge.asset.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Which requests reach the controllers, with tokens signed by a key pair made for the test. */
class IdentityTokenFilterTest {

    private static KeyPair identity;
    private static KeyPair stranger;
    private static IdentityTokenFilter filter;

    @BeforeAll
    static void keys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        identity = generator.generateKeyPair();
        stranger = generator.generateKeyPair();
        filter = new IdentityTokenFilter(true, Base64.getEncoder().encodeToString(identity.getPublic().getEncoded()));
    }

    private static String token(KeyPair keys, long expiresInMs) {
        return Jwts.builder().subject("user-1").expiration(new Date(System.currentTimeMillis() + expiresInMs))
                .signWith(keys.getPrivate()).compact();
    }

    private static MockHttpServletResponse run(String method, String path, String authorization) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        return response;
    }

    @Test
    void aValidIdentityTokenIsLetThrough() throws Exception {
        assertThat(run("GET", "/api/assets", "Bearer " + token(identity, 60_000)).getStatus()).isEqualTo(200);
    }

    @Test
    void missingForeignAndExpiredTokensAreRejected() throws Exception {
        assertThat(run("GET", "/api/assets", null).getStatus()).isEqualTo(401);
        assertThat(run("DELETE", "/api/assets/a-1", "Bearer " + token(stranger, 60_000)).getStatus()).isEqualTo(401);
        assertThat(run("GET", "/api/documents/d-1/preview", "Bearer " + token(identity, -60_000)).getStatus()).isEqualTo(401);
        assertThat(run("GET", "/api/vendors", "Basic dXNlcjpwYXNz").getStatus()).isEqualTo(401);
    }

    @Test
    void preflightsHealthAndNonApiPathsNeedNoToken() throws Exception {
        assertThat(run("OPTIONS", "/api/assets", null).getStatus()).isEqualTo(200);
        assertThat(run("GET", "/api/assets/health", null).getStatus()).isEqualTo(200);
        assertThat(run("GET", "/actuator/health", null).getStatus()).isEqualTo(200);
    }

    @Test
    void switchedOnWithoutAKeyFailsAtStartupInsteadOfLettingEverythingThrough() {
        assertThatThrownBy(() -> new IdentityTokenFilter(true, "")).isInstanceOf(IllegalStateException.class);
        assertThat(new IdentityTokenFilter(false, "")).isNotNull();
    }
}

package com.xmessenger.controllers.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JwtConfig {
    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }

    /**
     * By default, 'subject' param is User login.
     *
     * @param subject - User login.
     * @return JWT token string.
     */
    public String composeToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + this.getExpiration() * 1000))
                .sign(HMAC512(this.getSecret().getBytes()));
    }

    public String retrieveSubjectFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(this.getSecret().getBytes()))
                .build()
                .verify(token)
                .getSubject();
    }
}

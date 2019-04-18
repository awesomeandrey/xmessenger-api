package com.xmessenger.controllers.security.jwt.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xmessenger.model.database.entities.Role;
import com.xmessenger.model.database.entities.core.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class TokenProvider {
    private final String AUTHORITIES_KEY = "scopes";

    public static final String HEADER_NAME = "Authorization";
    public static final String HEADER_PREFIX = "Bearer ";

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * Generates JWT token based on secured user.
     *
     * @param authUser - secured user with specific authorities;
     * @return JWT token.
     */
    public String generateToken(UserDetails authUser) {
        final String[] authorities = authUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
        return this.generateToken(authUser.getUsername(), authorities);
    }

    /**
     * Generates JWT token based on database entity.
     *
     * @param appUser - AppUser DAO entity;
     * @return JWT token.
     */
    public String generateToken(AppUser appUser) {
        final String[] authorities = appUser.getRoles().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
        return this.generateToken(appUser.getUsername(), authorities);
    }

    public String getUsernameFromToken(String token) {
        return this.decodeJWT(token).getSubject();
    }

    public Set<Role> getClaimsFromToken(String token) {
        DecodedJWT decodedJWT = this.decodeJWT(token);
        return Arrays.stream(decodedJWT.getClaim(this.AUTHORITIES_KEY)
                .asArray(String.class))
                .map(String::toUpperCase)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * Adds specific HEADER_NAME for JWT authentication flow;
     *
     * @param response - outgoing HttpServletResponse;
     * @param token    - JWT token.
     */
    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.addHeader(HEADER_NAME, HEADER_PREFIX.concat(token));
    }

    /**
     * Extracts JWT token from incoming request;
     *
     * @param request - incoming HttpServletRequest;
     * @return JWT token.
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HEADER_NAME);
        if (header == null || !header.startsWith(HEADER_PREFIX)) return null;
        return header.replace(HEADER_PREFIX, "");
    }

    private String generateToken(String subject, String[] authorities) {
        return JWT.create()
                .withSubject(subject)
                .withArrayClaim(this.AUTHORITIES_KEY, authorities)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expiration * 1000))
                .sign(HMAC512(this.secret.getBytes()));
    }

    private DecodedJWT decodeJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC512(this.secret.getBytes());
        return JWT.require(algorithm).build().verify(token);
    }
}

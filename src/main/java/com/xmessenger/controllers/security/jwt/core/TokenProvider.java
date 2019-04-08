package com.xmessenger.controllers.security.jwt.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xmessenger.model.database.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * By default, 'subject' param is User login.
     * TODO - remove (used in gmail authentication flow);
     *
     * @param subject - User login.
     * @return JWT token string.
     */
    public String composeToken(String subject) {
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000))
                .sign(HMAC512(this.secret.getBytes()));
    }


    /**
     * Generates JWT token based on authenticated user.
     *
     * @param authUser - authenticated user with specific authorities;
     * @return JWT token.
     */
    public String generateToken(User authUser) {
        final String[] authorities = authUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
        return JWT.create()
                .withSubject(authUser.getUsername())
                .withArrayClaim(this.AUTHORITIES_KEY, authorities)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expiration * 1000))
                .sign(HMAC512(this.secret.getBytes()));
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
     * Adds specific header for JWT authentication flow;
     *
     * @param response - outgoing HttpServletResponse;
     * @param token    - JWT token.
     */
    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.addHeader(this.header, this.prefix.concat(token));
    }

    /**
     * Extracts JWT token from incoming request;
     *
     * @param request - incoming HttpServletRequest;
     * @return JWT token.
     */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(this.header);
        if (header == null || !header.startsWith(this.prefix)) return null;
        return header.replace(this.prefix, "");
    }

    private DecodedJWT decodeJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC512(this.secret.getBytes());
        return JWT.require(algorithm).build().verify(token);
    }
}

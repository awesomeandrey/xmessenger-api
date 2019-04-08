package com.xmessenger.controllers.security.jwt.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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

    public String retrieveSubjectFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(secret.getBytes()))
                .build()
                .verify(token)
                .getSubject();
    }


    public String generateToken(User authUser) {
        final String authorities = authUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return JWT.create()
                .withSubject(authUser.getUsername())
                .withClaim(this.AUTHORITIES_KEY, authorities)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + this.expiration * 1000))
                .sign(HMAC512(this.secret.getBytes()));
    }

    public UsernamePasswordAuthenticationToken getAuthentication(final String token, final Authentication existingAuth, final UserDetails userDetails) {

//        final JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
//
//        final Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
//
//        final Claims claims = claimsJws.getBody();
//
//        final Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);

        return null;
    }

    public void addTokenToResponse(HttpServletResponse response, String token) {
        response.addHeader(this.header, this.prefix.concat(token));
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(this.header);
        if (header == null || !header.startsWith(this.prefix)) return null;
        return header.replace(this.prefix, "");
    }
}

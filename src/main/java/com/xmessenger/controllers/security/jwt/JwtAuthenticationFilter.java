package com.xmessenger.controllers.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.model.database.entities.ApplicationUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter registers itself responsible for {"/api/login"} endpoint with POST method.
 * As such, whenever backend API gets a request to {"/api/login"}, the specialization of this filter
 * goes into action and handles the authentication attempt (through the attemptAuthentication method).
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl(WebSecurityConfig.API_BASE_PATH.concat("/login"));
        this.jwtConfig = jwtConfig;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        try {
            ApplicationUser user = new ObjectMapper().readValue(req.getInputStream(), ApplicationUser.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse response, FilterChain chain, Authentication auth) {
        String tokenSubject = ((User) auth.getPrincipal()).getUsername(), token = this.jwtConfig.composeToken(tokenSubject);
        response.addHeader(this.jwtConfig.getHeader(), this.jwtConfig.getPrefix() + token);
    }
}
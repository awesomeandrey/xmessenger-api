package com.xmessenger.controllers.security.jwt.filter;

import com.google.gson.Gson;
import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.security.user.Credentials;
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
import java.util.stream.Collectors;

/**
 * This filter registers itself responsible for {"/api/login"} endpoint with POST method.
 * As such, whenever backend API gets a request to {"/api/login"}, the specialization of this filter
 * goes into action and handles the authentication attempt (through the attemptAuthentication method).
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl(WebSecurityConfig.API_BASE_PATH.concat("/login"));
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {
        System.out.println(">>> attemptAuthentication()");

        try {
            String rawPayload = req.getReader().lines().collect(Collectors.joining());
            System.out.println("Raw payload: " + rawPayload.toString());

            Credentials credentials = new Gson().fromJson(rawPayload, Credentials.class);
            System.out.println("Credentials: " + credentials.toString());

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse response, FilterChain chain, Authentication auth) {
        System.out.println(">>> successfulAuthentication()");

        User authenticatedUser = (User) auth.getPrincipal();
        System.out.println("authenticatedUser: " + authenticatedUser.toString());

        String token = this.tokenProvider.generateToken(authenticatedUser);
        this.tokenProvider.addTokenToResponse(response, token);
    }
}
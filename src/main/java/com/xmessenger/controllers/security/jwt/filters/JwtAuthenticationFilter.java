package com.xmessenger.controllers.security.jwt.filters;

import com.google.gson.Gson;
import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.services.async.AsynchronousService;
import com.xmessenger.model.services.core.user.credentials.decorators.RawCredentials;
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
    private final AsynchronousService asynchronousService;
    private final Gson gson;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, AsynchronousService asynchronousService) {
        super.setFilterProcessesUrl(WebSecurityConfig.API_BASE_PATH.concat("/login"));
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.asynchronousService = asynchronousService;
        this.gson = new Gson();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        RawCredentials rawCredentials = null;
        try {
            String rawPayload = request.getReader().lines().collect(Collectors.joining());
            rawCredentials = this.gson.fromJson(rawPayload, RawCredentials.class);
        } catch (Exception ex) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getLocalizedMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(rawCredentials.getUsername(), rawCredentials.getPassword())
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        User authenticatedUser = (User) authentication.getPrincipal();
        String token = this.tokenProvider.generateToken(authenticatedUser);
        this.tokenProvider.addTokenToResponse(response, token);
        this.asynchronousService.renewLastLogin(authenticatedUser.getUsername());
    }
}
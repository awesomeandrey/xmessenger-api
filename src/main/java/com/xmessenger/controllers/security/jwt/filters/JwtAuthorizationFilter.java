package com.xmessenger.controllers.security.jwt.filters;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.database.entities.Role;
import com.xmessenger.model.services.async.AsynchronousService;
import com.xmessenger.model.util.Utility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final TokenProvider tokenProvider;
    private final AsynchronousService asynchronousService;

    public JwtAuthorizationFilter(AuthenticationManager authManager, TokenProvider tokenProvider, AsynchronousService asynchronousService) {
        super(authManager);
        this.tokenProvider = tokenProvider;
        this.asynchronousService = asynchronousService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!request.getRequestURI().startsWith(WebSecurityConfig.API_BASE_PATH)) {
            chain.doFilter(request, response);
            return;
        }
        Authentication authentication = null;
        try {
            authentication = this.getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return;
        }
        // Authenticated request propagates to target webservice endpoint;
        chain.doFilter(request, response);
        if (!request.getRequestURI().contains("logout")) {
            String username = (String) authentication.getPrincipal();
            this.asynchronousService.switchAppUserIndicator(username, true);
        }
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        String token = this.extractToken(request);
        String username = this.tokenProvider.getUsernameFromToken(token);
        Set<Role> roles = this.tokenProvider.getClaimsFromToken(token);
        return new UsernamePasswordAuthenticationToken(
                username, null, roles
        );
    }

    private String extractToken(HttpServletRequest request) {
        String token = this.tokenProvider.extractTokenFromRequest(request);
        if (Utility.isBlank(token)) {
            throw new IllegalArgumentException("Json Web Token required.");
        }
        return token;
    }
}
package com.xmessenger.controllers.security.jwt.filter;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.database.entities.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public JwtAuthorizationFilter(AuthenticationManager authManager, TokenProvider tokenProvider) {
        super(authManager);
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!request.getRequestURI().startsWith(WebSecurityConfig.API_BASE_PATH)) {
            chain.doFilter(request, response);
            return;
        }
        String token = this.tokenProvider.extractTokenFromRequest(request);
        if (token == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Json Web Token required.");
            return;
        }
        String username = null;
        Set<Role> roles = null;
        try {
            username = this.tokenProvider.getUsernameFromToken(token);
            roles = this.tokenProvider.getClaimsFromToken(token);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username, null, roles
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
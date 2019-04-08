package com.xmessenger.controllers.security.jwt.filter;

import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.model.util.Utility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Collections.emptyList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final TokenProvider tokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authManager, TokenProvider tokenProvider) {
        super(authManager);
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String token = this.tokenProvider.extractTokenFromRequest(req);
        if (token == null) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = this.tokenProvider.retrieveSubjectFromToken(token);
        if (Utility.isBlank(username)) return null;
        // Implicitly set 'authenticated' flag to true;
        return new UsernamePasswordAuthenticationToken(username, null, emptyList());
    }
}
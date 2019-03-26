package com.xmessenger.controllers.security.jwt;

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
    private final JwtConfig jwtConfig;

    public JwtAuthorizationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
        super(authManager);
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(this.jwtConfig.getHeader());
        if (header == null || !header.startsWith(this.jwtConfig.getPrefix())) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = this.getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(this.jwtConfig.getHeader());
        if (Utility.isBlank(token)) return null;
        token = token.replace(this.jwtConfig.getPrefix(), "");
        String username = this.jwtConfig.retrieveSubjectFromToken(token);
        if (Utility.isBlank(username)) return null;
        // Implicitly set 'authenticated' flag to true;
        return new UsernamePasswordAuthenticationToken(username, null, emptyList());
    }
}
package com.xmessenger.controllers.security.cors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter implements Filter {

    private final CorsBuilder corsBuilder;

    @Autowired
    public CustomCorsFilter(CorsBuilder corsBuilder) {
        this.corsBuilder = corsBuilder;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (CorsUtils.isCorsRequest(request)) {
            this.corsBuilder.enableCors(request, response);
            if (CorsUtils.isPreFlightRequest(request)) return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}

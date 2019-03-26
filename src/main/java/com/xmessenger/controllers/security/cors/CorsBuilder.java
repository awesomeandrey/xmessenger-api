package com.xmessenger.controllers.security.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CorsBuilder {
    @Value("${security.cors.origins}")
    private String origins;

    @Value("${security.cors.maxAge}")
    private String maxAge;

    public String getOrigins() {
        return this.origins;
    }

    public void enableCors(HttpServletRequest request, HttpServletResponse response) {
        String requestOrigin = request.getHeader("Origin");
        if (this.origins.contains(requestOrigin)) {
            response.setHeader("Access-Control-Allow-Origin", requestOrigin);
            response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
            response.setHeader("Access-Control-Max-Age", this.maxAge);
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, Cache-Control");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }
    }
}

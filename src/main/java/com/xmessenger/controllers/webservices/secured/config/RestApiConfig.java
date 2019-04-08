package com.xmessenger.controllers.webservices.secured.config;

import com.xmessenger.configs.WebSecurityConfig;
import com.xmessenger.controllers.webservices.secured.resftul.chatter.filters.ChatterValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfig {
    private final ChatterValidationFilter chatterValidationFilter;

    @Autowired
    public RestApiConfig(ChatterValidationFilter chatterValidationFilter) {
        this.chatterValidationFilter = chatterValidationFilter;
    }

    @Bean
    public FilterRegistrationBean chattingValidationFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(this.chatterValidationFilter);
        registrationBean.addUrlPatterns(WebSecurityConfig.API_BASE_PATH.concat("/chats/*"));
        return registrationBean;
    }
}

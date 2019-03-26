package com.xmessenger.configs;

import com.xmessenger.controllers.security.jwt.JwtAuthenticationFilter;
import com.xmessenger.controllers.security.jwt.JwtAuthorizationFilter;
import com.xmessenger.controllers.security.jwt.JwtConfig;
import com.xmessenger.controllers.security.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public final static String API_BASE_PATH = "/api";
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtConfig jwtConfig() {
        return new JwtConfig();
    }

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(API_BASE_PATH.concat("/**"))
                .authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), this.jwtConfig()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.jwtConfig()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .csrf()
                .disable()
                .cors()
                .and()
                .headers()
                .frameOptions()
                .sameOrigin();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(this.passwordEncoder());
    }
}
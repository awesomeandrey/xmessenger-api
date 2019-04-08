package com.xmessenger.configs;

import com.xmessenger.controllers.security.jwt.filter.JwtAuthenticationFilter;
import com.xmessenger.controllers.security.jwt.filter.JwtAuthorizationFilter;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.security.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public final static String API_BASE_PATH = "/api";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenProvider tokenProvider() {
        return new TokenProvider();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(API_BASE_PATH.concat("/**"))
                .authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), this.tokenProvider()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.tokenProvider()))
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
                .userDetailsService(this.userDetailsService())
                .passwordEncoder(this.passwordEncoder());
    }
}
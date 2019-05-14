package com.xmessenger.configs;

import com.xmessenger.controllers.security.jwt.filters.JwtAuthenticationFilter;
import com.xmessenger.controllers.security.jwt.filters.JwtAuthorizationFilter;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.security.user.details.UserDetailsServiceImpl;
import com.xmessenger.model.services.async.AsynchronousService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

    private final static String API_BASE_PATH_PATTERN = API_BASE_PATH.concat("/**");

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

    @Bean
    public AsynchronousService asynchronousService() {
        return new AsynchronousService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeRequests()
                .antMatchers(API_BASE_PATH_PATTERN)
                .authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), this.tokenProvider(), this.asynchronousService()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.tokenProvider()));
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

    @Bean
    public TaskExecutor asyncService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("async_service_thread");
        executor.initialize();
        return executor;
    }
}
package com.xmessenger.configs;

import com.xmessenger.controllers.security.basic.AuthenticationEntryPointImpl;
import com.xmessenger.controllers.security.jwt.filters.JwtAuthenticationFilter;
import com.xmessenger.controllers.security.jwt.filters.JwtAuthorizationFilter;
import com.xmessenger.controllers.security.jwt.core.TokenProvider;
import com.xmessenger.controllers.security.user.details.UserDetailsServiceImpl;
import com.xmessenger.model.database.entities.enums.Role;
import com.xmessenger.model.services.async.AsynchronousService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    public final static String API_BASE_PATH = "/api";
    public final static String ADMIN_API_BASE_PATH = "/admin-api";

    private final static String API_BASE_PATH_PATTERN = API_BASE_PATH.concat("/**");
    private final static String ADMIN_BASE_PATH_PATTERN = ADMIN_API_BASE_PATH.concat("/**");

    @Value("${credentials.admin.username}")
    private String ADMIN_USERNAME;

    @Value("${credentials.admin.password}")
    private String ADMIN_PASSWORD;

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
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    public AsynchronousService asynchronousService() {
        return new AsynchronousService();
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
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), this.tokenProvider(), this.asynchronousService()));
        http
                .authorizeRequests()
                .antMatchers(ADMIN_BASE_PATH_PATTERN)
                .authenticated()
                .and()
                .httpBasic()
                .authenticationEntryPoint(this.authenticationEntryPoint());
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
        auth
                .inMemoryAuthentication()
                .passwordEncoder(this.passwordEncoder())
                .withUser(this.ADMIN_USERNAME)
                .password(this.passwordEncoder().encode(this.ADMIN_PASSWORD))
                .roles(Role.ROLE_ADMIN.getAuthority().replaceFirst("ROLE_", "")); // ROLE_ADMIN cannot start with ROLE_ (it is automatically added);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
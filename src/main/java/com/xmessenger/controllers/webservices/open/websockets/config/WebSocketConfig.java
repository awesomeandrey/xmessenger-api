package com.xmessenger.controllers.webservices.open.websockets.config;

import com.xmessenger.controllers.security.cors.CorsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    public static final String MESSAGE_PREFIX = "/topic";

    private final CorsBuilder corsBuilder;

    @Autowired
    public WebSocketConfig(CorsBuilder corsBuilder) {
        this.corsBuilder = corsBuilder;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-configurator")
                .setAllowedOrigins(this.corsBuilder.getOrigins().split(","))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(MESSAGE_PREFIX);
    }
}
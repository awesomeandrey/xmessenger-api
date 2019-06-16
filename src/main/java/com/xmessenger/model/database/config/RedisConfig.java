package com.xmessenger.model.database.config;

import com.xmessenger.model.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import redis.clients.jedis.Protocol;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${spring.redis.url}")
    private String redisUrl;

    @Bean
    public JedisConnectionFactory jedisConnFactory() {
        try {
            URI redisUri = new URI(this.redisUrl);
            if (Utility.isBlank(this.redisUrl) || this.redisUrl.length() < 10) {
                redisUri = new URI(System.getenv("REDIS_URL"));
            }
            JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
            jedisConnFactory.setUsePool(true);
            jedisConnFactory.setHostName(redisUri.getHost());
            jedisConnFactory.setPort(redisUri.getPort());
            jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
            String password = this.getPassword(redisUri);
            if (Utility.isNotBlank(password)) {
                jedisConnFactory.setPassword(password);
            }
            return jedisConnFactory;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getPassword(URI uri) {
        String userInfo = uri.getUserInfo();
        if (Utility.isBlank(userInfo)) return null;
        String[] credentials = uri.getUserInfo().split(":", 2);
        if (credentials.length < 1) return null;
        return credentials[1];
    }
}
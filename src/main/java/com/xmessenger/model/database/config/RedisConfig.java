package com.xmessenger.model.database.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisPool jedisPool = new JedisPool();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMinIdle(1);
        poolConfig.setMaxIdle(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);

        jedisConnectionFactory.setHostName("ec2-52-209-84-212.eu-west-1.compute.amazonaws.com");
        jedisConnectionFactory.setPort(9989);
        jedisConnectionFactory.setPassword("p452d687123ce4e7cbb14c05739a596cbef15df379b6d88a26a100c512f69d8a4");

//        jedisConnectionFactory.setHostName(this.redisHost);
//        jedisConnectionFactory.setPort(this.redisPort);
        jedisConnectionFactory.setUsePool(true);

        System.out.println(">>> REDIS HOST: " + this.redisHost);
        System.out.println(">>> REDIS PORT: " + this.redisPort);

        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));
        return template;
    }
}
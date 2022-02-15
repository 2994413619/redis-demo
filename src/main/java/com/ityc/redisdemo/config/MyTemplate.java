package com.ityc.redisdemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class MyTemplate {

    @Bean
    public StringRedisTemplate ooxx(RedisConnectionFactory factory, ObjectMapper mapper) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return stringRedisTemplate;
    }

}

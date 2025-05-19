package com.mjc.groupware.common.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
	
	@Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // RedisTemplate의 Key, Value의 직렬화 방식을 설정 (= String : String 으로 사용할 예정)
        template.setKeySerializer(new StringRedisSerializer());  // key :: 문자열로 직렬화
        template.setValueSerializer(new StringRedisSerializer());  // value :: 문자열로 직렬화

        return template;
    }
	
}

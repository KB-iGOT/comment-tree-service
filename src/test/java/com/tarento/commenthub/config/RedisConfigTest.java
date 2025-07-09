package com.tarento.commenthub.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

class RedisConfigTest {

    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() throws Exception {
        redisConfig = new RedisConfig();
        setField(redisConfig, "redisHost", "localhost");
        setField(redisConfig, "redisPort", 6379);
    }

    void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testRedisConnectionFactory() {
        RedisConnectionFactory factory = redisConfig.redisConnectionFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof LettuceConnectionFactory);

        LettuceConnectionFactory lettuce = (LettuceConnectionFactory) factory;
        assertEquals("localhost", lettuce.getHostName());
        assertEquals(6379, lettuce.getPort());
    }

    @Test
    void testRedisTemplate() {
        RedisConnectionFactory connectionFactory = mock(RedisConnectionFactory.class);
        RedisTemplate<String, String> template = redisConfig.redisTemplate(connectionFactory);

        assertNotNull(template);
        assertSame(connectionFactory, template.getConnectionFactory());
        assertTrue(template.getKeySerializer() instanceof org.springframework.data.redis.serializer.StringRedisSerializer);
        assertTrue(template.getValueSerializer() instanceof org.springframework.data.redis.serializer.StringRedisSerializer);
    }
}

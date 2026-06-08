package com.certimaster.shared.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // Use String serializers for both key and value
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.create(builder -> builder
                .customize(mapper -> mapper.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS))
                .enableDefaultTyping(cacheTypeValidator()));

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("certification-list", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("certification-detail", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("provider-list", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("domain-tree", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("question-bank-list", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("question-statistics", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("progress-dashboard", defaultConfig.entryTtl(Duration.ofMinutes(3)));
        cacheConfigurations.put("progress-trends", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("progress-heatmap", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("progress-streaks", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig.entryTtl(Duration.ofMinutes(10)))
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    private PolymorphicTypeValidator cacheTypeValidator() {
        return BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.certimaster.")
                .allowIfSubType("java.time.")
                .allowIfSubType("java.util.")
                .allowIfSubType("java.math.")
                .allowIfSubType("org.springframework.data.domain.")
                .allowIfSubTypeIsArray()
                .build();
    }
}

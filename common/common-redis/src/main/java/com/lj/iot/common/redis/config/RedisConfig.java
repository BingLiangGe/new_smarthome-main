package com.lj.iot.common.redis.config;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lj.iot.common.redis.anno.RedisComponent;
import com.lj.iot.common.redis.anno.RedisListener;
import com.lj.iot.common.redis.properties.RedisCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisConfig {

    @Resource
    private RedisCacheProperties redisCacheProperties;
    @Resource
    private ApplicationContext applicationContext;

    @Bean
    RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    RedisSerializer<Object> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> j2jrs = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        j2jrs.setObjectMapper(objectMapper);

        return j2jrs;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(keySerializer());
        stringRedisTemplate.setValueSerializer(keySerializer());
        return stringRedisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

        log.info("==============================cacheManager===============================");
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));

        HashMap<String, Long> cacheNamesMap = redisCacheProperties.getCacheNames();
        if (cacheNamesMap == null) {
            cacheNamesMap = new HashMap<>();
        }
        Set<String> cacheNames = cacheNamesMap.keySet();

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : cacheNamesMap.entrySet()) {
            configMap.put(entry.getKey(), config.entryTtl(Duration.ofSeconds(entry.getValue())));
        }

        return RedisCacheManager.builder(redisConnectionFactory)
                .initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(configMap)
                .build();
    }


    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(keySerializer());
        redisTemplate.setHashKeySerializer(keySerializer());
        redisTemplate.afterPropertiesSet();
        redisTemplate.setValueSerializer(valueSerializer());
        redisTemplate.setHashValueSerializer(valueSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        String[] names = applicationContext.getBeanNamesForAnnotation(RedisComponent.class);

        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            Class clazz = bean.getClass();
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                RedisListener redisListener = AnnotationUtils.findAnnotation(method, RedisListener.class);
                if (redisListener == null) {
                    continue;
                }
                MessageListenerAdapter adapter = new MessageListenerAdapter(bean, method.getName());
                adapter.afterPropertiesSet();
                adapter.setSerializer(keySerializer());
                adapter.setStringSerializer(keySerializer());
                container.addMessageListener(adapter, new PatternTopic(redisListener.value()));
            }
        }
        return container;
    }
}
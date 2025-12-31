package com.lj.iot.common.redis.service.impl;

import com.alibaba.fastjson.JSON;
import com.lj.iot.common.redis.service.ICacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class CacheServiceImpl implements ICacheService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public <T> void setPermanent(String key, T value) {
        // 存入键值对
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.persist(key);
    }

    @Override
    public void del(String... keys) {
        for (String key : keys) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void del(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    @Override
    public Boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Boolean expireSeconds(String key, long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    @Override
    public <T> void add(String key, T value) {
        add(key, value, 0, TimeUnit.SECONDS);
    }

    @Override
    public <T> void addSeconds(String key, T value, long timeout) {
        add(key, value, timeout, TimeUnit.SECONDS);
    }

    @Override
    public <T> void add(String key, T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value);
        // 设置超时时间
        if (timeout != 0) {
            redisTemplate.expire(key, timeout, timeUnit);
        }
    }

    @Override
    public <T> T get(String key) {

        switch (redisTemplate.type(key)) {
            case STRING:
                return (T) redisTemplate.opsForValue().get(key);
            case LIST:
                return (T) redisTemplate.opsForList().range(key, 0, -1);
            case SET:
                return (T) redisTemplate.opsForSet().members(key);
            case ZSET:
                return (T) redisTemplate.opsForZSet().range(key, 0, -1);
            case HASH:
                if (key.split("\\|").length != 2) {
                    return (T) redisTemplate.opsForHash().entries(key);
                }
                return (T) redisTemplate.opsForHash().get(key.split("\\|")[0], key.split("\\|")[1]);
        }
        return null;
    }

    @Override
    public <T> boolean leftPush(String key, T value) {
        redisTemplate.opsForList().leftPush(key, value);
        return true;
    }

    @Override
    public <T> T rightPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public boolean lock(String key, int timeout) {
        boolean result = redisTemplate.opsForValue().setIfAbsent(key, 1, timeout, TimeUnit.MILLISECONDS);
        return result;
    }

    @Override
    public void convertAndSend(String topic, Object msg) {
        if (msg instanceof String) {
            stringRedisTemplate.convertAndSend(topic, msg);
            return;
        }
        stringRedisTemplate.convertAndSend(topic, JSON.toJSONString(msg));
    }
}

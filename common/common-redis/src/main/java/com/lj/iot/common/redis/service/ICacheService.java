package com.lj.iot.common.redis.service;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface ICacheService {

    <T> void setPermanent(String key,T value);

    /**
     * 删除Key
     *
     * @param keys
     */
    void del(String... keys);

    /**
     * 批量删除key
     *
     * @param keys
     */
    void del(Collection<String> keys);

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    Boolean exist(String key);

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @return
     */
    Boolean expireSeconds(String key, long timeout);

    /**
     * 设置过期时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 设置过期时间
     *
     * @param key
     * @param date
     * @return
     */
    Boolean expireAt(String key, Date date);

    /**
     * 添加
     *
     * @param key
     * @param value
     * @param <T>
     */
    <T> void add(String key, T value);

    /**
     * 添加
     *
     * @param key
     * @param value
     * @param timeout
     * @param <T>
     */
    <T> void addSeconds(String key, T value, long timeout);

    /**
     * 设置Key
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @param <T>
     */
    <T> void add(String key, T value, long timeout, TimeUnit timeUnit);

    /**
     * 获取值
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T get(String key);

    /**
     * 左边压入数据
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    <T> boolean leftPush(String key, T value);

    /**
     * 右边取出数据
     *
     * @param key
     * @param <T>
     * @return
     */
    <T> T rightPop(String key);

    boolean lock(String key, int timeout);

    void convertAndSend(String topic,Object msg);
}

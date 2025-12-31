package com.lj.iot.api.demo.redis;

import com.lj.iot.common.redis.anno.RedisComponent;
import com.lj.iot.common.redis.anno.RedisListener;

@RedisComponent
public class TestListener {

    @RedisListener("topic1")
    public void topic1(String msg){
        System.out.println(msg);
    }
    @RedisListener("topic1")
    public void topic12(String msg){
        System.out.println(msg);
    }
    @RedisListener("topic2")
    public void topic2(String msg){
        System.out.println(msg);
    }
}

package com.lj.iot.config.kafka.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Slf4j
@Configuration
public class KafkaProducter {
    @Resource
    private StreamBridge streamBridge;

    /**
     * 消息发送
     * @param topic
     * @param message
     * @return
     */
    public boolean sendMessage(String topic, String message) {
        try {
            boolean flag = streamBridge.send(topic, message);
            log.info("发送到kafka{}，topic={}, message={}", flag ? "成功" : "失败", topic, message);
            return flag;
        } catch (Exception e) {
            log.info("kafka异常，topic={}, message={}, exceptionMsg={}", topic, message, e.getLocalizedMessage());
            return false;
        }
    }
}

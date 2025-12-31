package com.lj.iot.common.mqtt.client.config;

import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.mqtt.client.core.MqttHandler;
import com.lj.iot.common.mqtt.client.properties.MqttClientProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Order(99999)
@Slf4j
@AllArgsConstructor
@Configuration
@ConditionalOnProperty(name = "mqtt.client.enabled", matchIfMissing = false)
public class MqttConfig {

    @Bean
    public MqttClientProperties mqttClientProperties() {
        return new MqttClientProperties();
    }

    @Bean
    @Order(99999)
    @ConditionalOnMissingBean(MqttHandler.class)
    public MqttHandler mqttHandler() {
        return (HandleMessage message) -> {
            log.info("消息主题：" + message.getTopic());
            log.info("消息内容：" + message.getBody());
        };
    }

    @Bean
    public MQTT mqtt(MqttClientProperties properties) {
        return new MQTT(properties);
    }
}

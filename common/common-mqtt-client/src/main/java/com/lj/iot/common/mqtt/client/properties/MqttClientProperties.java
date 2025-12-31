package com.lj.iot.common.mqtt.client.properties;

import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "mqtt.client")
public final class MqttClientProperties {

    private String host;
    private Integer port;
    private String clientId;
    private int pingInterval;
    private int keepAliveInterval = 60;
    private String[] defaultTopic;
    private String password;
    private long retryInterval = 1L;
    private boolean enabled;
    private MqttVersion protocolVersion = MqttVersion.MQTT_5;
}

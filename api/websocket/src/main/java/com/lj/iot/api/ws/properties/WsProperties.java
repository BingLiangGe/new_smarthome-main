package com.lj.iot.api.ws.properties;

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
@ConfigurationProperties(prefix = "ws")
public final class WsProperties {

    private Integer port;
    private int keepAliveInterval=60;
}

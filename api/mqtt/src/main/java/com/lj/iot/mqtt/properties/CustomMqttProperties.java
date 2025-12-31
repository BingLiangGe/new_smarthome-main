package com.lj.iot.mqtt.properties;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class CustomMqttProperties {

    private Integer port = 13077;

    /**
     * 为 0 表示不触发事件【单位秒】
     */
    private Integer readerIdleTimeSeconds = 10;
    /**
     * 为 0 表示不触发事件【单位秒】
     */
    private Integer writerIdleTimeSeconds = 0;
    /**
     * 为 0 表示不触发事件【单位秒】
     */
    private Integer allIdleTimeSeconds = 0;
}

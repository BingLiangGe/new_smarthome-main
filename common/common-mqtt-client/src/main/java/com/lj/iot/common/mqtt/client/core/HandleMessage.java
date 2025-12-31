package com.lj.iot.common.mqtt.client.core;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mz
 * @Date 2022/7/26
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HandleMessage {

    /**
     * topic
     */
    private String topic;

    /**
     * topic 路径上带的产品ID
     */
    private String topicProductId;

    /**
     * topic 路径上带的设备ID
     */
    private String topicDeviceId;
    /**
     * 消息ID
     */
    private Integer messageId;

    /**
     * 消息体
     */
    private JSONObject body;
}

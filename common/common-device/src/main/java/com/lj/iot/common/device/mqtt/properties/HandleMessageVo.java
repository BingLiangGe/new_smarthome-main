package com.lj.iot.common.device.mqtt.properties;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandleMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private String body;
}

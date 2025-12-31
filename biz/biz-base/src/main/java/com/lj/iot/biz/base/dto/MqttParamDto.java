package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttParamDto {

    /**
     * 消息ID
     */
    private String id;

    /**
     * 时间戳
     */
    private Long time;

    /**
     * 参数
     */
    private Object data;
}

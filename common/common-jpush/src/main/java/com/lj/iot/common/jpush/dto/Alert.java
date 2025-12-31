package com.lj.iot.common.jpush.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 主题
     */
    private String title;

    /**
     * 消息体
     */
    private Object body;
}

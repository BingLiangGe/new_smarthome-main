package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 发送语音
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SendVoiceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @NotNull(message = "射频设备不能为空")
    private String masterDeviceId;

    /**
     * 语音编码
     */
    @NotNull(message = "语音编码不能为空")
    private String code;

    private String orderNo;
}

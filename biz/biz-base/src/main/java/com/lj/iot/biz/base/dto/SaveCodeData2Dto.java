package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 发送红外码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCodeData2Dto {

    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;


    /**
     * 按键编码
     */
    @NotBlank(message = "按键编码不能为空")
    private String keyCode;

    /**
     * 射频码
     */
    @NotBlank(message = "射频码不能为空")
    private String codeData;
}

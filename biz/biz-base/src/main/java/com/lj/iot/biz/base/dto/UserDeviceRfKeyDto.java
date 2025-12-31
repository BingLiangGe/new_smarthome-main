package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送红外码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceRfKeyDto {

    /**
     * 设备ID
     */
    @NotNull(message = "设备不能ID为空")
    private String deviceId;
}

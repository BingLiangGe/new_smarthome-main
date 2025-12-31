package com.lj.iot.biz.base.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class UserDeviceBindDto {
    /**
     * 设备ID
     */
    @NotNull(message = "设备ID")
    private String deviceId;

    /**
     * 操作不能为空
     */
    @NotNull(message = "操作不能为空")
    private Boolean action;
}

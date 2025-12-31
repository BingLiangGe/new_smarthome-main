package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 控制插座、灯
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceContrDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @NotNull(message = "射频设备不能为空")
    private String masterDeviceId;

    /**
     *  0关  1开
     */
    @NotNull(message = "控制类型不能为空")
    private Integer type;
}

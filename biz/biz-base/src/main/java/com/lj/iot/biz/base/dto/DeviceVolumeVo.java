package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVolumeVo {

    /**
     * ID
     */
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;

    @NotBlank(message = "音量不能为空")
    private Integer value;

}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceActiveDto {

    /**
     * 设备mac地址
     */
    @NotBlank(message = "设备mac地址不能为空")
    private String macAddress;

    /**
     * 产品ID
     */
    @NotBlank(message = "设备产品ID不能为空")
    private String productId;

}
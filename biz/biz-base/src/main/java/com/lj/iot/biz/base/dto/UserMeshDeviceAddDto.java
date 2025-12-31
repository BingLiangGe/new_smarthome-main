package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备添加
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMeshDeviceAddDto {

    /**
     * 主控ID
     */
    @NotBlank(message = "主控ID不能为空")
    private String masterDeviceId;

    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    private boolean bind;
}

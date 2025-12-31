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
public class UserDeviceAddDto {

    /**
     * 主控ID
     */
    @NotBlank(message = "主控ID不能为空")
    private String masterDeviceId;

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    /**
     * 设备模型ID(添加IR/RF设备需要)
     */
    private Long modelId;

    private String deviceId;
    /**
     * 控制器设备ID
     */
    private String controlDeviceId="";

    private Boolean saveMesh;

    private long roomId;

    private String customName;
}

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
public class UserDeviceEditDto {

    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 设备名
     */
    @NotBlank(message = "设备名不能为空")
    private String customName;

    /**
     * 房间ID
     */
    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /**
     * modelId[红外切换模型]
     */
    private Long modelId;

    /**
     * 门锁密码
     */
    private String doorPwd;
}

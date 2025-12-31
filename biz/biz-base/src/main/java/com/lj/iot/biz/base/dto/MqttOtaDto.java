package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *  Ota参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqttOtaDto {
    /**
     * 升级包
     */
    @NotNull(message = "升级路径不能空")
    private String filepath;
    /**
     * 软件版本号
     */
    @NotNull(message = "软件版本号不能为空")
    private String softwareversion;
    /**
     * 硬件版本号
     */
    @NotNull(message = "硬件版本号不能为空")
    private String hardwareversion;
    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private String productId;
    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private String deviceId;


    private String details;

    private LocalDateTime time;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 *  Ota参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceOtaDto {
    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    private String  productId;
    /**
     * 设备ID
     */
    private ArrayList<String> deviceId;
    /**
     * 升级包
     */
    @NotNull(message = "升级路径不能空")
    private String filePath;
    /**
     * 软件版本号
     */
    @NotNull(message = "软件版本号不能为空")
    private String softWareVersion;
    /**
     * 硬件版本号
     */
    @NotNull(message = "硬件版本号不能为空")
    private String hardWareVersion;

    /**
     * 是否全选
     */
    @NotNull(message = "选项不能为空")
    private boolean select;
}

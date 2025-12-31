package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpgradeAddDto {

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    /**
     * 版本文件地址
     */
    @NotBlank(message = "版本文件地址不能为空")
    private String versionUrl;

    /**
     * 旧版本
     */
    @NotBlank(message = "旧版本号不能为空")
    private String oldVersion;

    /**
     * 新版本
     */
    @NotBlank(message = "新版本号不能为空")
    private String newVersion;

    /**
     * 升级包名称
     */
    @NotBlank(message = "升级包名称不能为空")
    private String updePackageName;

    /**
     * 升级包描述
     */
    private String updePackageDetails;

    /**
     * 硬件版本号
     */
    @NotBlank(message = "硬件版本号不能为空")
    private String hardWareVersion;
}

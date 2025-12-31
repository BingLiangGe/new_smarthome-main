package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品升级表
 *
 * @author xm
 * @since 2022-07-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_upgrade")
public class ProductUpgrade implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 版本文件地址
     */
    private String versionUrl;

    /**
     * 旧版本
     */
    private String oldVersion;

    /**
     * 新版本
     */
    private String newVersion;

    /**
     * 计划升级设备数
     */
    private Integer planCount;

    /**
     * 升级成功
     */
    private Integer successCount;

    /**
     * 升级失败
     */
    private Integer failureCount;

    /**
     * 是否已验证，验证后才能操作升级
     */
    private Boolean valid;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 升级包名称
     */
    private String updePackageName;

    /**
     * 升级包描述
     */
    private String updePackageDetails;

    /**
     * 硬件版本号
     */
    private String hardWareVersion;

    @TableField(exist = false)
    private String imagesUrl;

    @TableField(exist = false)
    private String productName;
}

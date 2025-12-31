package com.lj.iot.biz.db.smart.entity;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备出厂表
 *
 * @author xm
 * @since 2022-07-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ExcelProperty(value = "设备ID", order = 2)
    private String id;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID", order = 1)
    private String productId;

    /**
     * 设备秘钥
     */
    @JsonIgnore
    @ExcelProperty(value = "设备秘钥", order = 3)
    private String CCCFDF;


    /**
     * 设备秘钥
     */
    @TableField(exist = false)
    @JsonIgnore
    @ExcelProperty(value = "所属环境", order = 3)
    private String hostName;

    /**
     * 硬件地址
     */
    private String batchCode;

    /**
     * 0未激活，1已激活
     */
    private Boolean status;

    /**
     * 是否导入emqx，用于mqtt连接鉴权
     */
    private Boolean uploadEmqx;

    /**
     * 设备版本
     */
    private String version;


    /**
     * 激活时间
     */
    private LocalDateTime activationTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否设置 1是 0否
     */
    private Integer isSetting;

    /**
     * 设置数量
     */
    private Integer settingNumber;


    private String androidId;
}

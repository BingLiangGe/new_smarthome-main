package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * 设备出厂表
 * 
 *
 * @author xm
 * @since 2022-07-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicePageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String id;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 批次
     */
    private String batchCode;

    /**
     * 设备版本
     */
    private String version;

    /**
     * 0未激活，1已激活
     */
    private Boolean status;

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
}

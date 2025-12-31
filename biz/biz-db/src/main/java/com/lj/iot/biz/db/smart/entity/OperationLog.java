package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 用户设备日志表
 * </p>
 *
 * @author xm
 * @since 2022-12-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 设备名称
     */
    private String deviceId;

    /**
     * 产品Id
     */
    private String productId;

    /**
     * 产品类型（可能是子类型的）
     */
    private String productType;

    /**
     * 用户自定义辅助名称
     */
    private String customName;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 主控设备Id
     */
    private String masterDeviceId;

    /**
     * 信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     */
    private String signalType;

    /**
     * 设备物模型对应的值存储
     */
    private String params;

    /**
     * 设备状态在线1,离线0
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Byte action;

    @TableField(exist = false)
    private String startTime;

    @TableField(exist = false)
    private String endTime;
}

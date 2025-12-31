package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 *
 * 设备升级日志表
 * 
 *
 * @author xm
 * @since 2022-07-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_upgrade_log")
public class DeviceUpgradeLog implements Serializable {

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
     * 设备ID
     */
    private String deviceId;

    /**
     * 旧版本
     */
    private String oldVersion;

    /**
     * 新版本
     */
    private String newVersion;

    /**
     * 计划开始时间
     */
    private LocalDateTime startTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态0未开始1升级中2升级成功3升级失败
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

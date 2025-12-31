package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备调度表
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_device_schedule", autoResultMap = true)
public class UserDeviceSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 主控ID
     */
    private String masterDeviceId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 领捷产品Id
     */
    private String productId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 按键代码
     */
    private String keyCode;

    /**
     * 物理模型
     */
    @TableField(typeHandler = FastjsonTypeHandler.class, javaType = true)
    private ThingModel thingModel;

    /**
     * 表达式
     */
    private String cron;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间
     */
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;
}

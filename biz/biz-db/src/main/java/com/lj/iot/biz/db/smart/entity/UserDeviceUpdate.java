package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户设备表
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称
     */
    @TableId(value = "device_id")
    private String deviceId;

    /**
     * 领捷产品Id
     */
    private String productId;



    /**
     * 主控设备Id
     */
    private String masterDeviceId;

    /**
     * 主控设备产品Id
     */
    private String masterProductId;


    /**
     * 设备物模型对应的值存储
     */
    @TableField(typeHandler = FastjsonTypeHandler.class, javaType = true)
    private ThingModel thingModel;



    /**
     * 设备状态在线true,离线false
     */
    private Boolean status;


    /**
     * 物理设备Id
     */
    private String physicalDeviceId;


}

package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yuli
 * @Date 2022/7/21
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 设备状态 offline 离线 online 在线
     */
    private String status;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 主控设备ID
     */
    private String masterDeviceId;
    /**
     * 用户设备名称
     */
    private String customName;
    /**
     * 设备原始名称
     */
    private String deviceName;

    /**
     * 房间ID
     */
    private Long roomId;
    /**
     * 房间名称
     */
    private String roomName;
    /**
     * 设备类型 IR 红外 ，RF 射频 ，MESH 蓝牙MESH
     */
    private String signalType;
    /**
     * 设备模型数据
     */
    private String thingModel;

    /**
     * mq设备Id
     */
    private String physicalDeviceId;
    /**
     * 设备图片
     */
    private String imagesUrl;
}

package com.lj.iot.biz.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备原始名称
     */
    private String deviceName;
    /**
     * 用户自定义设备名称
     */
    private String customName;

    /**
     * 网关
     */
    private String masterDeviceId;

    /**
     * 领捷产品Id
     */
    private String productId;

    /**
     * 产品类型（可能是子类型的）
     */
    private String productType;

    /**
     * 顶级产品类型
     */
    private String topProductType;

    /**
     * 信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     */
    private String signalType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 实体设备ID
     */
    private String physicalDeviceId;

    /**
     * 设备图片地址链接
     */
    private String imagesUrl;

    /**
     * 房间ID
     */
    private Long roomId;
    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 设备物模型
     */
    private ThingModel thingModel;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 场景选中设备表示 false表示未选中，true 表示选中
     */
    private Boolean flag;

    /**
     * 设备状态
     */
    private Boolean status;


    /**
     * 设备状态
     */
    private boolean isDel;

    /**
     * model_id
     */
    private String modelId;


    @TableField(exist = false)
    private String modelStatus;

    @TableField(exist = false)
    private String checkStatus;

    /**
     * 智能房间锁秘钥
     */
    private String lockCCCFDF;
    /**
     * 智能房间锁授权码
     */
    private String lockAuthCode;
}

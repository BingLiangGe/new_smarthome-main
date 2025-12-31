package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author xm
 * @since 2023-01-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_device_mesh_key")
public class UserDeviceMeshKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备名称
     */
    private String deviceId;

    /**
     * 属性标识
     */
    private String identifier;

    /**
     * 属性值
     */
    private Integer value;

    /**
     * 绑定的场景ID
     */
    private Long sceneId;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 按键名称
     */
    private String keyName;

    /**
     * 产品Id
     */
    private String productId;

    /**
     * 产品thingModeKeyId
     */
    private Long productKeyId;
}

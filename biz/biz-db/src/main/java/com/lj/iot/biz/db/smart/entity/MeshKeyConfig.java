package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * 
 * Mesh设备按键配置
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mesh_key_config")
public class MeshKeyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 元素索引
     */
    private Integer elementIndex;

    /**
     * 模型ID
     */
    private Integer modelId;

    /**
     * 蓝牙设备主键id
     */
    private Long parentId;

    /**
     * 产品key
     */
    private String productId;

    /**
     * 目标设备主键id
     */
    private Long targetDeviceId;

    /**
     * 目标元素索引
     */
    private Integer targetElementIndex;

    /**
     * 目标发布地址
     */
    private Integer targetPubAddr;

    /**
     * 场景ID
     */
    private Long sceneId;
}

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
 * 场景设备表
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "scene_device", autoResultMap = true)
public class SceneDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 设备用户自定义名称
     */
    @TableField(exist = false)
    private String customName;

    /**
     * 房间名称
     */
    @TableField(exist = false)
    private String roomName;


    /**
     * 组ID
     */
    @TableField(exist = false)
    private String groupId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品ID
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
     * 设备图片
     */
    @TableField(exist = false)
    private String imagesUrl;


    @TableField(exist = false)
    private String signalType;

    /**
     * 物理模型
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private ThingModel thingModel;

    /**
     * 更新时间
     */
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;

    /**
     * 设备延时
     */
    private Integer delayedTime;

    /**
     * 组name
     */
    @TableField(exist = false)
    private String groupName;


    /**
     * modelId
     */
    @TableField(exist = false)
    private Long modelId;

    @TableField(exist = false)
    private String modelStatus;
}

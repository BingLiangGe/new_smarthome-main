package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-03-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("device_group")
public class DeviceGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组id
     */
    private String groupId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 组名称
     */
    private String groupName;
    /**
     * 用户id
     */
    private String userId;

    @TableField(exist = false)
    private List<DeviceGroup> list;

    @TableField(exist = false)
    private String deviceName;

    @TableField(exist = false)
    private String roomName;

    @TableField(exist = false)
    private String imagesUrl;

    @TableField(exist = false)
    private String customName;

}

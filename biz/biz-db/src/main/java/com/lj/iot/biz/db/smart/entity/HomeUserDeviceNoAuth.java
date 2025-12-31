package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("home_user_device_no_auth")
public class HomeUserDeviceNoAuth{
    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 设备id
     */
    private Long deviceOrSceneId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Long createdBy;

    private Long updatedBy;

    /**
     * 0:设备权限，1:场景权限
     */
    private Integer authType;

    private Long homeUserId;

}

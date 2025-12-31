package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-02-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_messages")
public class SystemMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型：1网关升级 2:网关子设备升级3:sos消息
     */
    private Integer type;

    /**
     * 消息
     */
    private String messages;

    /**
     * 0未读
     */
    private Integer readType;

    /**
     * 用户id
     */
    private String userId;

    private Integer homeId;

    private Integer roomId;

    private String inType;
    private String homeName;
    private String roomName;
    private Long joinId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(exist=false)
    private String fomTime;



}

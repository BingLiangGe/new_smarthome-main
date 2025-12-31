package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 闹钟
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_clock")
public class UserClock implements Serializable {

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

    /**
     * 设置时间
     */
    private LocalDateTime settingTime;

    /**
     * 状态 0未处理 1已处理
     */
    private Integer clockStatus;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 家和用户关联申请表
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("home_user_join")
public class HomeUserJoin implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 成员用户ID
     */
    private String memberUserId;

    /**
     * 成员手机号码
     */
    private String memberMobile;

    /**
     * apply：申请；invite：邀请
     */
    private String action;

    /**
     * 进度
     */
    private String state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist=false)
    private String homeName;

    @TableField(exist=false)
    private String userName;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 酒店用户账号表
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hotel_user_account")
public class HotelUserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    private String id;

    /**
     * 登录账号[可能不是手机号]
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 性别;0:未知;1:男;2:女
     */
    private Integer gender;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 微信授权成功标志
     */
    private String openId;

    /**
     * 实际用户ID
     */
    private String actualUserId;

    /**
     * 1：主账号  2：子账号
     */
    private String type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


    @TableField(exist = false)
    private List<Long> roleIdList;


    private Integer isWorker;
}

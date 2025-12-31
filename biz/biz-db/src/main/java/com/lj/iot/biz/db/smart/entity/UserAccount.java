package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户账号表
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_account")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id")
    private String id;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 性别;0:未知;1:男;2:女
     */
    private Integer gender;

    /**
     * 手机号码(登录账号)
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 昵称
     */
    //@JsonIgnore
    private String openId;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 1：主账号  2：可编辑子账号 3：不可编辑子账号
     */
    private String type;

    /**
     * 主账号用户ID
     */
    private String actualUserId;

    /**
     * 过期时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)//序列化器
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)//反序列化器
    private LocalDateTime expires;
    /**
     * 创建时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)//序列化器
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)//反序列化器
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonSerialize(using = LocalDateTimeSerializer.class)//序列化器
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)//反序列化器
    private LocalDateTime updateTime;

    /**
     * 苹果登录唯一id
     */
    private String appleId;

    /**
     * 驗證碼
     */
    private String code;
}

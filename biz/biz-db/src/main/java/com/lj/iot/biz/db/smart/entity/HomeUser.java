package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 家和用户关联表
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("home_user")
public class HomeUser implements Serializable {

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
     * 是否主账号(1:是;0:否),默认否
     */
    private Boolean isMain;

    /**
     * 邀请人用户Id
     */
    private String memberUserId;
    /**
     * 被邀请人手机号码
     */
    private String memberMobile;

    /**
     * 账户类型
     */
    private String type;


    /**
     * 是否是默认的家(1:是;0:否),默认否
     */
    private Boolean isDefaultHome;

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
}

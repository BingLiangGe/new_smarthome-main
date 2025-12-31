package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 酒店和用户关联表
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hotel_user")
public class HotelUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private String hotelUserId;

    /**
     * 酒店Id
     */
    private Long hotelId;

    /**
     * 是否主账号(1:是;0:否),默认否
     */
    private Boolean isMain;

    /**
     * 成员用户ID
     */
    private String memberUserId;

    /**
     * 是否是默认(1:是;0:否),默认否
     */
    private Boolean isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

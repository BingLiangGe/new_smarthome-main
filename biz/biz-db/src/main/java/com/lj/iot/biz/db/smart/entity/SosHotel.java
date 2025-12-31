package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2024-01-03
 */
@Builder
@Getter
@Setter
@TableName("sos_hotel")
public class SosHotel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 内容
     */
    private String username;

    /**
     * 酒店id
     */
    private Long hotelId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

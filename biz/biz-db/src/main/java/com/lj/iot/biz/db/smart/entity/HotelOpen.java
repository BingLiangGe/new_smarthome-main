package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-05-16
 */
@Getter
@Setter
@TableName("hotel_open")
public class HotelOpen implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 开房时间
     */
    private LocalDateTime openTime;

    /**
     * 退房时间
     */
    private LocalDateTime closeTime;

    /**
     * 酒店房间（家id）号
     */
    private Long homeId;

    /**
     * 操作用户
     */
    private String optionUser;

    /**
     * 开房状态
     */
    private Integer openStatus;
}

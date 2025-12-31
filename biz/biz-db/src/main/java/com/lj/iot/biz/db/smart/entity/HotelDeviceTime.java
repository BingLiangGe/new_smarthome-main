package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

/**
 * <p>
 * 设备续费时间
 * </p>
 *
 * @author xm
 * @since 2023-04-08
 */
@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hotel_device_time")
public class HotelDeviceTime implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */

    private String id;


    /**
     * 到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime datetime;


    /**
     * 到期时间 格式
     */
    @TableField(exist = false)
    private Long longDatetime;
}

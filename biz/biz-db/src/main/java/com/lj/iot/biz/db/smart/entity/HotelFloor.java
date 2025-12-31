package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 楼层
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("hotel_floor")
public class HotelFloor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id(主账号)
     */
    private String hotelUserId;

    /**
     * 酒店
     */
    private Long hotelId;

    /**
     * 楼层名称
     */
    private String floorName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

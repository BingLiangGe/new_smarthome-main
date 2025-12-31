package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 外卖订单表
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("notice")
public class Notice implements Serializable {

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
     * 酒店ID
     */
    private Long hotelId;

    /**
     * 房间Id
     */
    private Long roomId;

    /**
     * 主控设备Id
     */
    private String deviceId;

    /**
     * 备注
     */
    private String remarks;


    /**
     * 1:商品；2：呼叫前台 ；3： sos
     */
    private Integer type;

    /**
     * 订单状态;0:未处理;1:已处理
     */
    private Boolean state;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

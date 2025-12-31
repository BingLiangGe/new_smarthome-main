package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 *
 * 外卖订单表
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("take_out_order")
public class TakeOutOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建人Id
     */
    private String createdBy;

    /**
     * 是否删除(1:是;0:否),默认否
     */
    private Boolean isDel;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 更新人Id
     */
    private String updatedBy;

    /**
     * 主控设备Id
     */
    private String deviceId;

    /**
     * 主控设备名称
     */
    private String deviceName;

    /**
     * 主控设备原始名称
     */
    private String deviceOriginalName;

    /**
     * 商品Id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品单位
     */
    private String goodsUnit;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 家名称
     */
    private String homeName;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 房间Id
     */
    private Long roomId;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 订单状态;1:未处理;2:已处理
     */
    private Integer state;

    /**
     * 用户Id
     */
    private String userId;
}

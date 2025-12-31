package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 
 * 音乐畅听卡订单表
 * 
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("music_order")
public class MusicOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单总价;单位元,保留两位小数
     */
    private BigDecimal amount;

    /**
     * 音乐产品Id
     */
    private Long musicId;

    /**
     * 订单编号;规则:M+yyyyMMddHHmmss+3位流水号
     */
    private String orderNo;

    /**
     * 绑定的设备Id
     */
    private String deviceId;

    /**
     * 支付方式:1:微信;2:支付宝;
     */
    private Integer payType;

    /**
     * 预支付交易会话标识(来自第三方支付)
     */
    private String prepayId;

    /**
     * 订单状态;1:待支付;2:已支付;3:已取消;
     */
    private Integer state;

    /**
     * 交易id(来自第三方支付)
     */
    private String transactionId;

    /**
     * 用户Id
     */
    private String userId;
}

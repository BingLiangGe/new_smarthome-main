package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicOrderPageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
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

    /**
     * 音乐产品名称
     */
    private String musicName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;
}

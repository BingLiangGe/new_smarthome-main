package com.lj.iot.common.pay.wx;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class WxPayResultVo implements Serializable {

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 交易id
     */
    private String transactionId;

    /**
     * outRefundNo
     */
    private String outRefundNo;

    /**
     * refundId
     */
    private String refundId;

    /**
     * refundStatus
     */
    private String refundStatus;

    /**
     * successTime
     */
    private LocalDateTime successTime;
}

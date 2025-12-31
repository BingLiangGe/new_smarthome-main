package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteMusicOrderDto {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;


    /**
     * 交易号
     */
    @NotBlank(message = "交易号不能为空")
    private String transactionId;
}

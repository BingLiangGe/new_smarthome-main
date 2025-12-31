package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String deviceId;

    private String productId;

    private String CCCFDF;
}

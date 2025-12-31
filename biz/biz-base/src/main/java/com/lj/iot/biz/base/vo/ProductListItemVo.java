package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListItemVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String productName;
    private String productType;
    private String controlProductId;
    private String signalType;
    private Long relationDeviceTypeId;
    private String imagesUrl;
}

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
public class UnBindMeshDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型
     */
    private String productCode;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 图片
     */
    private String imagesUrl;
}

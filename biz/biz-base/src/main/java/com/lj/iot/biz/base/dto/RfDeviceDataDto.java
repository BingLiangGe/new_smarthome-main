package com.lj.iot.biz.base.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 射频设备实体保存请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfDeviceDataDto {
    /**
     * 设备品牌id
     */

    private String brandId;

    /**
     * 设备品牌名称
     */
    private String brandName;

    /**
     * 用户自定义辅助名称
     */
    private String customName;

    /**
     * 用户自定义名称
     */
    private String deviceName;



    /**
     * 产品类型Id
     */
    private Long productTypeId;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 领捷产品Id
     */
    private String productId;

    /**
     * 主控设备Id
     */
    private String masterDeviceId;

    /**
     * 设备型号id
     */
    private Long modelId;

    /**
     * 设备型号名称
     */
    private String modelName;

    /**
     * 家房间Id
     */
    private Long roomId;

    /**
     * 信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     */
    private String signalType;


}

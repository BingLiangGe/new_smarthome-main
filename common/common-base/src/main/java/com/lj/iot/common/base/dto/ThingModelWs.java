package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThingModelWs implements Serializable {

    private ThingModel thingModel;

    private String deviceId;

    private String productType;

    /**
     * 顶级产品类型
     */
    private String topProductType;


    /**
     * 信号类型;IR:红外;RF:射频;Mesh:Mesh设备;Master:主控设备
     */
    private String signalType;
}

package com.lj.iot.biz.base.dto;


import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * mesh设备属性下发
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMeshDataDto {

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 属性值
     * [{
     * "identifier":"powerstate",
     * "value":"0"
     * },
     * ...
     * ]
     */
    @NotNull(message = "属性值")
    private ThingModel thingModel;
}

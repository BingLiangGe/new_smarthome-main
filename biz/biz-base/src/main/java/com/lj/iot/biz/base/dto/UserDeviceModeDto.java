package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDeviceModeDto {

    /**
     * ID
     */
    @NotNull(message = "模式ID不能为空")
    private Long id;

    /**
     * 模式名
     */
    private String modeName;

    /**
     * 物理模型
     */
    private ThingModel thingModel;
}

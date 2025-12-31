package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneDeviceDto {

    /**
     * 设备ID名称
     */
    // @NotBlank(message = "设备ID名称不能为空")
    private String deviceId;

    /**
     * 物理模型
     */
    // @NotNull(message = "物理模型不能为空")
    private ThingModel thingModel;

    /**
     * 设备延时
     */
    private Integer delayedTime = 0;
}

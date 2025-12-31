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
public class HandleUserDeviceDto<T> {
    private T userDevice;
    private ThingModel changeThingModel;
    private String keyCode;
    private Integer keyIdx;
    /**
     * 延时时间
     */
    private Integer delayedTime;
}

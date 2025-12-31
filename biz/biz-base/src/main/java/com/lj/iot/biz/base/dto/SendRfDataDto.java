package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 发送红外码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendRfDataDto {
    /**
     * 设备ID
     */
    @NotNull(message = "射频设备不能为空")
    private String deviceId;

    /**
     * 按键代码不能为空
     */
    @NotNull(message = "按键代码不能为空")
    private String keyCode;

    /**
     * 属性设置
     */
    @NotNull(message = "属性设置不能为空")
    private ThingModel thingModel;

    /**
     * 射频码[学码测试的时候把测试的码发过来]
     */
    private String codeData;
}

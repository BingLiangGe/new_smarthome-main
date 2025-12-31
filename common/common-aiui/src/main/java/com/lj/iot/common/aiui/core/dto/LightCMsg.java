package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LightCMsg {
    private Integer Saturation;


    private Integer Hue;

    private Integer value;


    public static LightCMsg VALUE(Integer saturation, Integer hue, Integer value) {
        LightCMsg msg = new LightCMsg();
        msg.setSaturation(saturation);
        msg.setHue(hue);
        msg.setValue(value);
        return msg;
    }
}

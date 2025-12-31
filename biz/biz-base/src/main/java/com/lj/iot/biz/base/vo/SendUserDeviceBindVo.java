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
public class SendUserDeviceBindVo implements Serializable {

    private static final long serialVersionUID = 1L;
//    /**
//     * 设备ID
//     */
//    private String deviceId;

    /**
     * 主设备ID
     */
    private String physicalDeviceId;

    /**
     * 控制设备的属性key
     */
    private String identifier;


}

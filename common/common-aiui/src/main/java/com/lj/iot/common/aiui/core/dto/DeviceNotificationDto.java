package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author tyj
 * @date   2023-6-29 13:50:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceNotificationDto {

    //主控
    private String masterDeviceId;

    //用户ID
    private Long homeId;

    // intface name
    private String intentName;

    private String deviceId;


}

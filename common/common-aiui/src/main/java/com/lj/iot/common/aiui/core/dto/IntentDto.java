package com.lj.iot.common.aiui.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentDto {

    //主控
    private String masterDeviceId;

    //用户ID
    private String userId;

    /**
     * {
     * "name": "Device.OnOff",
     * "score": 1,
     * "confirmationStatus": "NONE",
     * "slots": {
     * "OnOff": {
     * "confirmationStatus": "NONE",
     * "moreValue": null,
     * "name": "OnOff",
     * "normValue": "TurnOn",
     * "value": "打开"
     * },
     * "device": {
     * "confirmationStatus": "NONE",
     * "moreValue": null,
     * "name": "device",
     * "normValue": "空调",
     * "value": "空调"
     * }* 			}
     * }
     */

    private String intentName;


    private String callMsg;

    /**
     * 命中语料
     */
    private String text;

    /**
     * 回复内容
     */
    private String answer;


    private Map<String, Slot> slots;


    private String type;

    @Data
    public static class Slot {
        private String name;
        private String normValue;
        private String value;
        private String type;
        //private String moreValue;
        //private String confirmationStatus;
    }
}

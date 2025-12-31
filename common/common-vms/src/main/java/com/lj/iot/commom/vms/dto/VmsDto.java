package com.lj.iot.commom.vms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmsDto {
    /**
     * 手机号
     */
    String mobile;
    /**
     * 家庭名
     */
    String homeName;
    /**
     * 设备名称
     */
    String deviceName;
    /**
     * 用户名
     */
    String userName;
    /**
     * 房间名
     */
    String roomName;

    String type;

    String masterDeviceId;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 智能门锁验证密码
 * @author tyj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoorPwdDto {

    /**
     * 设备 ID
     */
    @NotNull(message = "设备id不能为空")
    private String deviceId;

    /**
     * 门锁密码
     */
    @NotNull(message = "密码不能为空")
    private String doorPwd;

    /**
     * 验证码
     */
    private String code;

}

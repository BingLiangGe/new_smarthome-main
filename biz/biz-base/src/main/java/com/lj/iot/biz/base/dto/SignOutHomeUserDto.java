package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SignOutHomeUserDto {

    /**
     * 家用户ID
     */
    @NotNull(message = "家用户ID不能为空")
    private String homeUserId;

    /**
     * 家Id
     */
    @NotNull(message = "家Id")
    private long homeId;

    /**
     * 用户Id
     */
    @NotNull(message = "用户Id")
    private String userId;
}

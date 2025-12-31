package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class InviteUserDto {
    private static final long serialVersionUID = 1L;

    /**
     * 家ID
     */
    @NotNull(message = "家ID不能为空")
    private long homeId;

    /**
     * 被邀请人电话
     */
    @NotNull(message = "被邀请人电话不能为空")
    private String userMobile;
}

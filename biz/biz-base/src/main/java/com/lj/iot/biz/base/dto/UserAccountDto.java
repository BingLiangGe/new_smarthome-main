package com.lj.iot.biz.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
public class UserAccountDto {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @NotNull(message = "昵称不能为空")
    private String nickname;

    /**
     * 性别;0:未知;1:男;2:女
     */
    @NotNull(message = "性别不能为空")
    private int gender = 0;

    /**
     * 头像地址
     */
    private String avatarUrl;
}

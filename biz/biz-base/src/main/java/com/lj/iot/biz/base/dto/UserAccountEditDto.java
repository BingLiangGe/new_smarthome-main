package com.lj.iot.biz.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class UserAccountEditDto {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @NotNull(message = "昵称不能为空")
    private String nickname;

    /**
     * 头像
     */
    private String avatarUrl;
}

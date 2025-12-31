package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SosContactAddDto {

    /**
     * 家庭id
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    /**
     * 联系人类型
     */
    @NotBlank(message = "联系人类型不能为空")
    private String contactType;

    /**
     * 电话号码
     */
    @NotBlank(message = "电话号码不能为空")
    private String phoneNumber;

    /**
     * 联系人名称
     */
    @NotBlank(message = "联系人名称不能为空")
    private String username;
}

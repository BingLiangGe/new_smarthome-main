package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SosContactEditDto {

    /**
     * 主键ID 新增不传，修改必填
     */
    @NotNull(message = "ID不能为空")
    private Long id;

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

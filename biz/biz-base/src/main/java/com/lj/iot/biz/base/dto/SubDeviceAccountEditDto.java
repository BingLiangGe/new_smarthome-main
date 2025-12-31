package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubDeviceAccountEditDto {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String deviceId;

    /**
     * 编辑状态
     */
    @NotNull(message = "编辑状态不能为空")
    private Boolean editFlag;
}

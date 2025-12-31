package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultHomeDto {
    private static final long serialVersionUID = 1L;

    /**
     * 是否默认家 false 为非true 为是
     */
    @NotNull(message = "默认字段不能为空")
    private boolean defaultHome;
    /**
     * 家ID
     */
    @NotNull(message = "家ID不能空")
    private Long homeId;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyToHomeDto {
    /**
     * 家ID
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    private String userId;
}

package com.lj.iot.common.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysConfigAddDto {

    /**
     * key
     */
    @NotBlank(message = "KEY 不能为空")
    private String paramKey;

    /**
     * value
     */
    @NotBlank(message = "VALUE 不能为空")
    private String paramValue;

    /**
     * 备注
     */
    private String remark;
}

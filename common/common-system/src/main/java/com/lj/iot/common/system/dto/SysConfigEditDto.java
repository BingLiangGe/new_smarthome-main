package com.lj.iot.common.system.dto;

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
public class SysConfigEditDto {

    @NotNull(message = "ID不能为空")
    private Long id;
    /**
     * key
     */
    @NotBlank(message = "KEY不能为空")
    private String paramKey;

    /**
     * value
     */
    @NotBlank(message = "VALUE不能为空")
    private String paramValue;

    /**
     * 备注
     */
    private String remark;
}

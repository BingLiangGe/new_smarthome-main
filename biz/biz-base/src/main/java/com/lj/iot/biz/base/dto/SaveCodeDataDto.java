package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送红外码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveCodeDataDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 射频码
     */
    @NotBlank(message = "射频码不能为空")
    private String codeData;
}

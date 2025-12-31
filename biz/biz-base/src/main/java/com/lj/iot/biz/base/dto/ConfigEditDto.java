package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigEditDto {

    /**
     * Key不能为空
     */
    @NotBlank(message = "key不能")
    private String dictionaryKey;

    /**
     * 值
     */
    @NotBlank(message = "值不能为空")
    private String dictionaryValue;

}

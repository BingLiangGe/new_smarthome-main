package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 设备属性键值
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesDataDto {
    /**
     * 属性值
     */
    @NotNull(message = "属性值不能为空")
    private Object value;

    /**
     * 属性key
     */
    @NotBlank(message = "属性key不能为空")
    private String identifier;

}

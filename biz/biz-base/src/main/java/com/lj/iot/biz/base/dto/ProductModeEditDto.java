package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModel;
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
public class ProductModeEditDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 模式代码
     */
    @NotBlank(message = "模式代码不能为空")
    private String modeCode;

    /**
     * 模式名
     */
    @NotBlank(message = "模式名不能为空")
    private String modeName;

    /**
     * 属性值
     */
    @NotNull(message = "属性值不能为空")
    private ThingModel thingModel;
}

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
public class ProductThingModelKeyEditDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    /**
     * 按键名称
     */
    @NotBlank(message = "按键名称不能为空")
    private String keyName;

    /**
     * 红外按键下标(没有传个0)
     */
    @NotNull(message = "红外按键下标不能为空")
    private Integer keyIdx;
    /**
     * 按键对应属性
     */
    @NotBlank(message = "按键对应属性不能为空")
    private String identifier;

    /**
     * 按键代码（必填，这个跟语音还有射频代码有关）
     */
    @NotBlank(message = "按键代码不能为空")
    private String keyCode;


    /**
     * 模式 (=,+,-,loop)
     */
    @NotBlank(message = "模式不能为空")
    private String mode;


    /**
     * 步长(默认值)
     */
    @NotNull(message = "步长不能为空")
    private Integer step;
}

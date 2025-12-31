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
public class SkillEntityEditDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 意图名称
     */
    @NotBlank(message = "意图名称不能为空")
    private String intentName;

    /**
     * 插槽名称
     */
    @NotBlank(message = "插槽名称不能为空")
    private String entityName;

    /**
     * 插槽代码
     */
    @NotBlank(message = "插槽代码不能为空")
    private String entityKey;
    /**
     * 支持产品类型(为空表示支持所有产品类型)
     */
    private String supportProductType;
}

package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModelProperty;
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
public class SkillEntityEntryEditDto {

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
     * 插槽代码
     */
    @NotBlank(message = "插槽代码不能为空")
    private String entityKey;

    /**
     * 词条代码
     */
    @NotBlank(message = "词条代码不能为空")
    private String entryKey;

    /**
     * 词条名称
     */
    @NotBlank(message = "词条名称不能为空")
    private String entryName;

    /**
     * 按键代码
     */
    @NotBlank(message = "按键代码不能为空")
    private String keyCode;

    /**
     * 属性(固定属性，不传走逻辑填词)
     */
    private ThingModelProperty thingModelProperty;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityAliasAddDto {

    /**
     * 实体别名
     */
    private String entityKey;

    /**
     * 实体别名
     */
    @NotBlank(message = "实体别名")
    private String entityName;

    /**
     * 设备类型不能为空
     */
    private String deviceType;

    /**
     * 属性类型
     */
    @NotBlank(message = "属性类型不能为空")
    private String attrType;

}

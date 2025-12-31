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
public class ProductTypeAddDto {

    /**
     * 父级ID
     */
    private String parentId;

    /**
     * 设备类别代码
     */
    @NotBlank(message = "设备类别代码不能为空")
    private String productType;

    /**
     * 设备类型名称
     */
    @NotBlank(message = "设备类型名称不能为空")
    private String productTypeName;

    /**
     * 类型图片
     */
    @NotBlank(message = "类型图片不能为空")
    private String imagesUrl;
}

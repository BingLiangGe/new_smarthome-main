package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeEditDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 设备类别英文名称
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

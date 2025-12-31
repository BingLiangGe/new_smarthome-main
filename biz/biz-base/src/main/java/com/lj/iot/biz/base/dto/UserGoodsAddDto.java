package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGoodsAddDto {

    /**
     * 商品名
     */
    @NotBlank(message = "商品名不能为空")
    private String goodsName;

    /**
     * 商品别名
     */
    private String goodsAlias;

    /**
     * 商品单位
     */
    @NotBlank(message = "商品单位不能为空")
    private String unit;

    /**
     * 商品数量
     */
    @NotNull(message = "商品数量不能为空")
    @Min(value = 0,message = "数量要大于零")
    private Integer quantity;

    /**
     * true:上架   false：下架
     */
    @NotNull(message = "上下架不能为空")
    private Boolean state;


    private String images;
}

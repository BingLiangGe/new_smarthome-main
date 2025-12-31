package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicProductEditDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;
    /**
     * 卡的数量
     */
    @NotNull(message = "卡的数量不能为空")
    private Integer count;

    /**
     * 产品封面
     */
    @NotBlank(message = "产品封面不能为空")
    private String coverUrl;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String musicName;

    /**
     * 单价;单位元,保留两位小数
     */
    @DecimalMin(value = "0", inclusive = false, message = "单价不能为空")
    private BigDecimal price;
}

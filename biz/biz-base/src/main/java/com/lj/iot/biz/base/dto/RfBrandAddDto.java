package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RfBrandAddDto {

    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    private String brandName;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdDto {

    /**
     * 产品ID
     */
    @NotBlank(message = "ID 不能为空")
    private String productId;
}

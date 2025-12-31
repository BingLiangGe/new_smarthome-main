package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdPageDto extends PageDto {

    /**
     * 产品ID
     */
    private String productId;
}

package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class IrModelDto {

    /**
     * 设备类型ID
     */
    @NotNull(message = "设备类型ID不能为空")
    private Long deviceTypeId;

    /**
     * 品牌ID
     */
    @NotNull(message = "品牌ID不能为空")
    private Long brandId;
}

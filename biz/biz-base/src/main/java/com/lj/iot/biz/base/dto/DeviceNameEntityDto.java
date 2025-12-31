package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceNameEntityDto {

    /**
     * 主键,自动生成
     */
    private Long id;

    /**
     * 实体别名
     * 多个别名用逗号隔开
     */
    @NotNull(message ="实体不能为空" )
    private String entityAlias;

    /**
     * 实体名称
     */
    @NotNull(message ="实体名称不能为空" )
    private String entityName;

    /**
     * 产品ID
     */
    @NotNull(message ="产品ID不能为空" )
    private String productId;
}


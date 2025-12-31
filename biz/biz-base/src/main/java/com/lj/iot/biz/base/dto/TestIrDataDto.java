package com.lj.iot.biz.base.dto;


import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 测试红外码请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  TestIrDataDto {

    /**
     * 主控设备ID
     */
    @NotBlank(message = "主控设备ID不能为空")
    private String masterDeviceId;

    /**
     * 模型ID不能为空
     */
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空")
    private String productId;

    /**
     * 模型数据
     */
    @NotNull(message = "模型数据不能为空")
    private ThingModel thingModel;


    /**
     * 按键key
     */
    @NotNull(message = "必填字段按键key")
    private Integer keyIndex;
}

package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.ThingModel;
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
public class ProductAddDto {

    /**
     * 产品类型Id
     */
    @NotNull(message = "产品类型ID不能为空")
    private Long productTypeId;

    /**
     * 信号类型;IR:红外;RF:射频;MESH 蓝牙,MASTER 主控
     */
    @NotBlank(message = "信号类型不能为空")
    private String signalType;

    /**
     * 产品代码
     */
    //@NotBlank(message = "产品代码不能为空")
    private String productCode;

    /**
     * 控制器产品ID
     */
    private String controlProductId;
    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    /**
     * 产品图片
     */
    @NotBlank(message = "产品图片不能为空")
    private String imagesUrl;

    /**
     * 物模型对象
     */
    @NotNull(message = "物理模型不能为空")
    private ThingModel thingModel;

    /**
     * 信号类型为;IR:红外;RF:射频，需要关联对应的设备类型表
     */
    private Long relationDeviceTypeId;
}

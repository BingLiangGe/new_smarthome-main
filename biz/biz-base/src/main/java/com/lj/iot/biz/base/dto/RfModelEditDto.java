package com.lj.iot.biz.base.dto;

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
public class RfModelEditDto {


    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;
    /**
     * 型号名称
     */
    @NotBlank(message = "型号名称不能为空")
    private String modelName;

    /**
     * 编码类型
     */
    @NotBlank(message = "编码类型不能为空")
    private String codeType;

    /**
     * 设备品牌ID
     */
    @NotNull(message = "设备品牌不能为空")
    private Long deviceBrandId;

    /**
     * 设备类型ID
     */
    @NotNull(message = "设备类型不能为空")
    private Long deviceTypeId;

    /**
     * 头数据
     */
    @NotBlank(message = "头数据不能为空")
    private String headData;

    /**
     * 开始零长时间
     */
    @NotNull(message = "开始零长时间不能为空")
    private Integer startZeroTime;

    /**
     * 单位时间（微秒）
     */
    @NotNull(message = "单位时间不能为空")
    private Integer unitTime;

    /**
     * 发送组数
     */
    @NotNull(message = "发送组数不能为空")
    private Integer sentCount;
}

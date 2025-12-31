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
public class RfKeyAddDto {

    /**
     * 设备型号Id
     */
    @NotNull(message = "设备型号Id不能为空")
    private Long deviceModelId;

    /**
     * 按键名称
     */
    @NotBlank(message = "按键名称不能为空")
    private String keyName;

    /**
     * 学码描述
     */
    @NotBlank(message = "学码描述不能为空")
    private String remark;
}

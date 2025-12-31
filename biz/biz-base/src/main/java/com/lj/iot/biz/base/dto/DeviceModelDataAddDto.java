package com.lj.iot.biz.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author ^_^
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelDataAddDto {

    /**
     * 设备ID
     */
    @NotNull(message = "关联设备Id不能为空")
    private String deviceId;

    /**
     * 色温值
     */
    @NotNull(message = "色温值不能为空")
    private Integer colorTemplate;

    /**
     * 亮度值
     */
    @NotNull(message = "亮度值不能为空")
    private Integer brightness;

    /**
     * 模式类型:1阅读模式，2影院模式，3，睡眠模式，4，工作模式
     */
    @NotNull(message = "模式类型不能为空")
    private String modelType;

    /**
     * 是否生效，0，生效，1失效
     */
    @NotNull(message = "是否生效不能为空")
    private String takeEffect;


    /**
     * 模式中文名称
     */
    @NotNull(message = "模式中文名称不能为空")
    private String modelName;

    @JsonIgnore
    private String userId;
}

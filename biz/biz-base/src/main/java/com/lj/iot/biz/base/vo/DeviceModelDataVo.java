package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author ^_^
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 色温值
     */
    private Integer colorTemplate;

    /**
     * 亮度值
     */
    private Integer brightness;

    /**
     * 模式类型:1阅读模式，2影院模式，3，睡眠模式，4，工作模式
     */
    private String modelType;

    /**
     * 是否生效，0，生效，1失效
     */
    private String takeEffect;


    /**
     * 模式中文名称
     */
    private String modelName;
}

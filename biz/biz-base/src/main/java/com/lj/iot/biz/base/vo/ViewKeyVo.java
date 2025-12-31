package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewKeyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 按键名称
     */
    private String keyName;
    /**
     * 属性标识
     */
    private String identifier;
    /**
     * 模式;=,+,-,loop
     */
    private String model = "=";
    /**
     * 领捷设备类型Id
     */
    private long deviceTypeId = 0;
}

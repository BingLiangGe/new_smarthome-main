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
public class ProductThingModelKeyVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 按键名称
     */
    private String keyName;

    /**
     * 按键下标
     */
    private Integer keyIdx;

    /**
     * 属性标识
     */
    private String identifier;

    /**
     * 按键代码
     */
    private String keyCode;
}

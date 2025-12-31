package com.lj.iot.biz.base.vo;


import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 按键代码
     */
    private String keyCode;

    /**
     * 物理模型
     */
    private ThingModel thingModel;

    /**
     * 更新时间
     */
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;

}

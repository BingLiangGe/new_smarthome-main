package com.lj.iot.biz.base.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineSceneListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 场景数据
     */
    private AuthSceneVo sceneVo;

    /**
     * 场景定时条件数据
     */
    private SceneScheduleVo sceneScheduleVo;

    /**
     * 场景设备数据
     */
    private SceneDeviceVo sceneDeviceVo;
}

package com.lj.iot.biz.base.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneDetailVo<D, S> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    private Long id;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 主控Id
     */
    private String masterId;

    /**
     * 主控名字
     */
    private String masterDeviceName;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景图片
     */
    private String sceneIcon;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 口令
     */
    private String command;

    /**
     * 最近一次执行时间
     */
    private LocalDateTime lastExecutionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 场景设备信息
     */
    private List<D> sceneDeviceVos;


    /**
     * 场景设备
     */
    private List<S> sceneScheduleVos;

}

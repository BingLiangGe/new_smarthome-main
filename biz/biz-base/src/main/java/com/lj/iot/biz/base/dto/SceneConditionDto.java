package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneConditionDto {
    /**
     * 家Id
     */
    @NotNull(message ="家ID不能为空")
    private Long homeId;

    /**
     * 条件参数
     *  {"days": [], "hour": 18, "minute": 55}
     *  {"text": "我要睡了"}
     */
    private String params;

    /**
     * 场景Id
     */
    private Long sceneId;

    /**
     * 动作类型  condition/timer 定时 condition/voice语音
     */
    @NotNull(message = "动作类型")
    private String uri;

}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneActionDto {
    /**
     * 家Id
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    /**
     * 动作扩展信息
     * {"coverUrl": "https://file.lj-smarthome.com/iot/b04267214bd1411c9c19de663d672e77.png?t=1646907879437", "roomName": "大厅", "sceneName": "", "deviceName": "大门窗帘"}
     * {"sceneName": "离家模式"}
     */
    @NotNull(message = "动作扩展不能为空")
    private String info;

    /**
     * 动作参数
     * {"deviceId": 23118, "properties": [{"name": "powerstate_1", "value": 0, "displayName": "关闭"}]}
     * {"sceneId": 1992}
     */
    @NotNull(message = "动作参数不能为空")
    private String params;

    /**
     * 场景Id
     */
    private Long sceneId;

    /**
     * 动作类型
     * action/device/property 设备属性设置
     * action/scene/trigger 情景内添加场景
     */
    @NotNull(message = "动作类型不能为空")
    private String uri;

    /**
     * 关联设备id或场景ID
     */
    @NotNull(message = "关联设备id或场景ID不能为空")
    private String targetId;
}

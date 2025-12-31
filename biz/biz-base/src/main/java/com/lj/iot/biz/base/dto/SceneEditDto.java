package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneEditDto {

    /**
     * 场景ID
     */
    @NotNull(message = "场景ID不能为空")
    private Long sceneId;

    /**
     * 场景名称
     */
    @NotBlank(message = "场景名称不能为空")
    private String sceneName;

    /**
     * 主控Id  例a,b
     */
    private String masterId;

    /**
     * 场景图片
     */
    private String sceneIcon;

    /**
     * 命令
     */
    @NotBlank(message = "命令不能为空")
    private String command;

    /**
     * 设备信息
     */
    @NotNull(message = "设备信息不能为空")
    @Valid
    private List<SceneDeviceDto> sceneDevices;

    /**
     * 调度信息
     */
    @NotNull(message = "调度信息不能为空")
    @Valid
    private List<SceneScheduleDto> sceneScheduleList;
}

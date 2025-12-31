package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneAddDto {

    /**
     * 家庭ID
     */
    @NotNull(message = "家庭ID不能为空")
    private Long homeId;

    /**
     * 场景名称
     */
    @NotBlank(message = "场景名称不能为空")
    private String sceneName;

    /**
     * 命令
     */
    @NotBlank(message = "命令不能为空")
    private String command;


    /**
     * 场景图片
     */
    private String sceneIcon;


    /**
     * 主控Id  例a,b
     */
    private String masterId;

    /**
     * 设备信息
     */
    @NotNull(message = "执行不能为空")
    @Valid
    private List<SceneDeviceDto> sceneDevices;

    /**
     * 调度信息
     */
    @NotNull(message = "定时信息不能为空")
    @Valid
    private List<SceneScheduleDto> sceneScheduleList;
}

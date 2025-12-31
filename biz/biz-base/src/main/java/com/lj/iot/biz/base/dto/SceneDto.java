package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneDto {
    /**
     * 场景ID 修改时必填
     */
    private Long id;
    /**
     * 场景名称
     */
    @NotNull(message = "场景名称不能为空")
    private String sceneName;
    /**
     * 音乐名称
     */
    private String musicName;
    /**
     * 音乐地址URL
     */
    private String musicUrl;
    /**
     * 启用状态
     */
    private Boolean enable = true;
    /**
     * 主控id多个主控用逗号隔开a,b
     */
    private String masterId;
    /**
     * 条件
     */
    @NotNull(message = "条件不能为空")
    private List<SceneConditionDto> conditions = new ArrayList<>();
    /**
     * 动作
     */
    @NotNull(message = "动作不能为空")
    private List<SceneActionDto> actions = new ArrayList<>();
    /**
     * 场景类型 0:一键执行;1:自动化
     */
    @NotNull(message = "场景类型不能为空")
    private Boolean type;

}

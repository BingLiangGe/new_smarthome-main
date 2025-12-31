package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 按键绑定场景
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeshKeyBindSceneDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 场景ID
     */
    @NotNull(message = "场景ID不能为空")
    private Long sceneId;
}

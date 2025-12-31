package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineSceneDto {

    /**
     * ID
     */
    @NotBlank(message = "主控设备ID不能为空")
    private String masterDeviceId;


    /**
     * 场景ID
     */
    private Long sceneId;

}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 学习射频码参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyRfData2Dto {
    /**
     * 射频设备ID
     */
    @NotBlank(message = "射频设备ID不能为空")
    private String deviceId;

    /**
     * 射频按键代码
     */
    @NotBlank(message = "射频按键代码不能为空")
    private String keyCode;
}

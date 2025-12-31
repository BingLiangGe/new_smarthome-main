package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学习射频码参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyRfDataDto {
    /**
     * 射频设备ID
     */
    @NotBlank(message = "射频设备ID不能为空")
    private String deviceId;

    /**
     * 射频按键主键Id
     */
    @NotNull(message = "射频按键主键Id不能为空")
    private Long userRfKeyId;
}

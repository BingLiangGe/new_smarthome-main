package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 保存射频码参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveRfDataDto {
    /**
     * 射频设备ID
     */
    @NotNull(message = "射频设备ID不能为空")
    private String deviceId;

    /**
     * 射频按键主键Id
     */
    @NotNull(message = "射频按键主键Id不能为空")
    private Long userRfKeyId;

    /**
     * 射频码
     */
    @NotBlank(message = "射频码不能为空")
    private String data;
}

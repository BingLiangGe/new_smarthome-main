package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeshLockEditDto {

    /**
     * 设备号
     */
    @NotBlank(message = "设备号")
    private String deviceId;

    /**
     * 别名
     */
    @NotBlank(message = "别名")
    private String customName;
}

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
public class MeshLockAddDto {

    /**
     * 设备号
     */
    @NotBlank(message = "设备号")
    private String lockMac;

    /**
     * 房间id
     */
    @NotBlank(message = "房间id")
    private String keyGroupId;

    /**
     * 密钥
     */
    @NotBlank(message = "密钥")
    private String CCCFDF;


    /**
     * 鉴权码
     */
    @NotBlank(message = "鉴权码")
    private String authCode;
}

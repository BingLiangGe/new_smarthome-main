package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 设备添加
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchMeshDeviceDto {

    /**
     * 主控ID
     */
    @NotBlank(message = "主控ID不能为空")
    private String masterDeviceId;

    /**
     * 不传表示所有
     */
    private String productId;
}

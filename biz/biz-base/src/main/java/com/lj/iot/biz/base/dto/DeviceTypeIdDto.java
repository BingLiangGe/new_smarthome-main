package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeIdDto {

    /**
     * 设备类型ID
     */
    @NotNull(message = "设备类型ID不能为空")
    private Long deviceTypeId;

}

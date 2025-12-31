package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 *  学习射频码参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteTopoDto {
    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private List<SuDeviceDto> deviceIds;
    /**
     * 主控ID
     */
    @NotNull(message = "主控ID不能为空")
    private String MasterDeviceId;
}

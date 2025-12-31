package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 设备添加
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceEditBatchDto {

    /**
     * 设备数据
     */
    @Valid
    @NotNull(message = "设备数据不能为空")
    private List<UserDeviceEditDto> list;
}

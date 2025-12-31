package com.lj.iot.biz.base.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DeviceBindDto {
    /**
     * 被绑定的设备集合
     */
    @NotEmpty(message = "被绑定的设备集合不能为空")
    private List<String> deviceIds;

    /**
     * 绑定设备ID
     */
    @NotNull(message = "绑定设备ID不能为空")
    private String deviceId;

    /**
     * true 新增
     * false 更新
     */
    @NotNull(message = "操作")
    private Boolean action;

}

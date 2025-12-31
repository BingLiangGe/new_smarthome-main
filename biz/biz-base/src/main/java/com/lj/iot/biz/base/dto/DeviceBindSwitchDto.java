package com.lj.iot.biz.base.dto;


import io.reactivex.rxjava3.annotations.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DeviceBindSwitchDto {

    /**
     * 绑定设备ID
     */
    @NotNull(message = "绑定设备ID不能为空")
    private String deviceId;


    private String groupId;

}

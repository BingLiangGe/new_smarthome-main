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
public class SwitchGroupsDto {


    /**
     * 旧id
     */
    @NotNull(message = "旧组id不能为空")
    private String beforeGroupId;

    /**
     * 新id
     */
    @NotNull(message = "新组id不能为空")
    private String newGroupId;

    /**
     * 绑定设备ID
     */
    @NotNull(message = "绑定设备ID不能为空")
    private String deviceId;


}

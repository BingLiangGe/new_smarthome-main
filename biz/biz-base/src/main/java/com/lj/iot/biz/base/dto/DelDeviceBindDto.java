package com.lj.iot.biz.base.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DelDeviceBindDto {
    /**
     * 多联多控组ID
     */
    @NotNull(message = "多联多控组ID")
    private String groupId;
}

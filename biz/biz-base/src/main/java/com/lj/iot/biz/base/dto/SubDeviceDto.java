package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubDeviceDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 家ID
     */
    @NotNull(message = "家ID不能为空")
    private long homeId;

    /**
     * 房间ID
     */
    private long roomId;
}

package com.lj.iot.biz.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
public class HomeRoomAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 家Id
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

    /**
     * 房间名称
     */
    @NotNull(message = "房间名称不能为空")
    private String roomName;
}

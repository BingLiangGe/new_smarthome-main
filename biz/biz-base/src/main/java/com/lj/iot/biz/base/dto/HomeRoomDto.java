package com.lj.iot.biz.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper = true)
public class HomeRoomDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;

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

    /**
     * 用户Id
     */
    @NotNull(message = "用户ID不能为空")
    private String userId;
}

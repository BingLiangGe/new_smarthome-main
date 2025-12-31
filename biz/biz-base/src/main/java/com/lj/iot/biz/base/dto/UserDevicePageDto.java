package com.lj.iot.biz.base.dto;

import com.lj.iot.common.base.dto.PageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDevicePageDto extends PageDto {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 房屋ID
     */
    private Long homeId;

    /**
     * 主控ID，传入这个值查询这个主控下的所有设备，包括主控自己
     */
    private String masterDeviceId;

    /**
     * 信号类型
     */
    private String signalType;
}

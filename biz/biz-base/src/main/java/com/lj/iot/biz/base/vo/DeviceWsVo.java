package com.lj.iot.biz.base.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceWsVo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 房间名字
     */
    private String roomName;

    /**
     * 设备名
     */
    private String customName;

    /**
     * 家名称
     */
    private String homeName;

    /**
     * 账号
     */
    private String mobile;

}

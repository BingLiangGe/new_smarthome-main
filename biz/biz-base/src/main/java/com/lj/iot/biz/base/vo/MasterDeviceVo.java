package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDeviceVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  主控设备封面
     */
    private String coverUrl;
    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 在线离线状态
     * offline 离线 online 在线
     */
    private String status;
}

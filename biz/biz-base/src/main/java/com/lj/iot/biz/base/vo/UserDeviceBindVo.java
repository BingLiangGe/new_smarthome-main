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
public class UserDeviceBindVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 用户自定义辅助名称
     */
    private String customName;

    /**
     * 用户自定义名称
     */
    private String deviceName;


    /**
     * 房间名
     */
    private String roomName;

    /**
     * 图片
     */
    private String imagesUrl;

    /**
     * 0表示未绑定，1为已绑定
     */
    private Boolean flag;
}

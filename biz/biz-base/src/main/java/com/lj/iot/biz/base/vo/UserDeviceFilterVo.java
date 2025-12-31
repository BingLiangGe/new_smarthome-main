package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 过滤设备用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceFilterVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备名称
     */
    private String deviceId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 用户自定义辅助名称
     */
    private String customName;

    /**
     * 用户自定义名称
     */
    private String deviceName;

    private Boolean status;

    /**
     * 智能房间锁秘钥
     */
    private String lockCCCFDF;
    /**
     * 智能房间锁授权码
     */
    private String lockAuthCode;
}

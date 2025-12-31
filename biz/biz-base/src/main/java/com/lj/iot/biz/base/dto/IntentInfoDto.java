package com.lj.iot.biz.base.dto;

import lombok.Data;

@Data
public class IntentInfoDto {

    private static final long serialVersionUID = 1L;

    /**
     * 支持设备类型（多个用,隔开。为空表示支持全部设备）
     */
    private String supportDeviceType;

    /**
     * 不支持设备（多个用,隔开，为空表示全部支持）
     */
    private String opposeDeviceType;

    /**
     * 属性标识
     */
    private String identifier;
}


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
public class SeachDeviceVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String deviceId;

    private String imagesUrl;

    private String customName;

    private String deviceName;

    private String homeName;
    private String hotelName;
    private String floorName;

    private String mobile;

}

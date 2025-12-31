package com.lj.iot.biz.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String thingModel;

    private String latitude;

    private String longitude;

    private Integer heartRate;

    private BigDecimal temperature;

    private String bloodPressure;

    private Integer bloodOxygen;

    private String address;

    private String roomName;

    private Integer status;

    private Integer bindCount;

    private String deviceId;

    private String imagesUrl;

    private String version;

    private Integer radius;

    private String settingLng;

    private String settingLat;

    private String watchStatus;

    private Integer roomId;

    private String customName;
}

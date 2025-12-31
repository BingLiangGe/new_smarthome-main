package com.lj.iot.biz.base.vo;

import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneSendInfoVo implements Serializable {

    private String deviceId;

    private Object type;

    private String deviceType;
}

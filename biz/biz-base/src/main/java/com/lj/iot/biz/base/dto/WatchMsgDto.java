package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class WatchMsgDto {

    /**
     * 设备 ID
     */
    private String deviceId;

    /**
     * 数据
     */
    private String data;
}

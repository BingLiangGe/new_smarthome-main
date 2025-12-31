package com.lj.iot.common.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HwScheduleParamDto {
    private Long scheduleId;

    private String businessId;

    private String cron;
}

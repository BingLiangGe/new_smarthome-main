package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleParamDto {

    /**
     * "user_clock","user_device_schedule"  的ID
     */
    private Long scheduleId;

    private String deviceId;

    private String cron;
}

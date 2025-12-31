package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneScheduleDto {

    /**
     * 是否开启
     */
    @NotNull(message = "是否开启不能为空")
    private Boolean enable;

    /**
     * cron 表达式
     */
    private String cron;
}

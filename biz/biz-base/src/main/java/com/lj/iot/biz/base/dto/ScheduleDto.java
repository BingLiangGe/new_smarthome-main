package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

    /**
     * 0-23
     */
    @NotNull(message = "小时不能为空")
    private Integer hour;

    /**
     * 0-59
     */
    @NotNull(message = "分钟不能为空")
    private Integer minute;

    /**
     * 星期天:1 星期一:2  ... 星期六:7
     */
    @NotNull(message = "分钟不能为空")
    private List<Integer> days;
}

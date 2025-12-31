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
public class SceneJobParamDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 场景可以设置多个时间调度，下面为调度ID
     */
    @NotNull(message = "调度ID不能为空")
    private Long scheduleId;

    private String cron;

    /**
     * 星期
    private List<Integer> daysOfWeek;
    *//**
     * 年
     *//*
    private int year;

    *//**
     * 月
     *//*
    private int month;

    *//**
     * 日
     *//*
    private int day;

    *//**
     * 小时
     *//*
    private int hour;

    *//**
     * 分钟
     *//*
    private int minus;*/
}

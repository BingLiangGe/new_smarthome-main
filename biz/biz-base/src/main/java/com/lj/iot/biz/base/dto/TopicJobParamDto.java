package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author mz
 * @Date 2022/7/19
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicJobParamDto {

    /**
     * ID
     */
    //@NotNull(message = "ID不能为空")
    private String topic;


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

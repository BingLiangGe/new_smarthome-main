package com.lj.iot.api.job.util;

import org.quartz.CronScheduleBuilder;
import org.quartz.ScheduleBuilder;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class QuartzUtil {

    public static final String KEY_OF_SCENE_ID = "SCENE_ID";
    public static final String KEY_OF_SCHEDULE_ID = "SCHEDULE_ID";
    public static final String KEY_OF_CLOCK_ID = "CLOCK_ID";
    public static final String KEY_OF_CANCEL_ORDER_NO = "CANCEL_ORDER_NO";
    public static final String KEY_OF_COMPLETED_ORDER_NO = "COMPLETED_ORDER_NO";
    public static final String KEY_OF_ROOM_15_MIN_SEND_VOICE = "ROOM_15_MIN_SEND_VOICE";
    public static final String KEY_OF_ROOM_5_MIN_SEND_VOICE = "ROOM_5_MIN_SEND_VOICE";
    public static final String KEY_OF_LAST_REMINDER_SEND_VOICE = "LAST_REMINDER_SEND_VOICE";
    public static final String KEY_OF_CLOSE_DEVICES = "CLOSE_DEVICES";
    public static final String KEY_OF_OPEN_DEVICES = "OPEN_DEVICES";
    public static final String KEY_OF_OPEN_CONTINUATION_ORDERS_DEVICES = "OPEN_CONTINUATION_ORDERS_DEVICES";

    public static final String KEY_OF_ORDER_ROOM_SERVICE_JOB = "ORDER_ROOM_SERVICE_JOB";

    public static final byte SUN = 1;
    public static final byte MON = SUN << 1;
    public static final byte TUE = MON << 1;
    public static final byte WED = TUE << 1;
    public static final byte THU = WED << 1;
    public static final byte FRI = THU << 1;
    public static final byte SAT = FRI << 1;

    public static String sceneJobName(Long sceneId, Long conditionId) {
        return String.format(Locale.getDefault(), "JOB:SCENE:%1$d:%2$d", sceneId, conditionId);
    }

    public static String sceneTriggerName(Long sceneId, Long conditionId) {
        return String.format(Locale.getDefault(), "TRIGGER:SCENE:%1$d:%2$d", sceneId, conditionId);
    }

    public static String sceneGroupName(Long sceneId) {
        return String.format(Locale.getDefault(), "GROUP:SCENE:%1$d", sceneId);
    }

    public static String scheduleGroupName(String deviceId) {
        return String.format(Locale.getDefault(), "GROUP:SCHEDULE:%s", deviceId);
    }

    public static String clockGroupName(String deviceId) {
        return String.format(Locale.getDefault(), "GROUP:CLOCK:%s", deviceId);
    }

    public static String scheduleJobName(Long scheduleId, String deviceId) {
        return String.format(Locale.getDefault(), "JOB:SCHEDULE:%s:%s", deviceId, scheduleId);
    }

    public static String clockJobName(Long scheduleId, String deviceId) {
        return String.format(Locale.getDefault(), "JOB:CLOCK:%s:%s", deviceId, scheduleId);
    }

    public static String scheduleTriggerName(Long scheduleId, String deviceId) {
        return String.format(Locale.getDefault(), "TRIGGER:SCHEDULE:%s:%s", deviceId, scheduleId);
    }

    public static String clockTriggerName(Long scheduleId, String deviceId) {
        return String.format(Locale.getDefault(), "TRIGGER:CLOCK:%s:%s", deviceId, scheduleId);
    }

    public static String deviceScheduleJobName(long deviceId, long scheduleId) {
        return String.format(Locale.getDefault(), "JOB:DS:%1$d:%2$d", deviceId, scheduleId);
    }

    public static String deviceScheduleTriggerName(Long deviceId, Long scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:DS:%1$d:%2$d", deviceId, scheduleId);
    }

    public static String deviceScheduleGroupName(Long deviceId) {
        return String.format(Locale.getDefault(), "GROUP:DS:%1$d", deviceId);
    }

    public static String hwCancelOrderJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:CANCELORDER:%s:%s", orderNo, scheduleId);
    }

    public static String hwCancelOrderTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:CANCELORDER:%s:%s", orderNo, scheduleId);
    }

    public static String hwCancelOrderGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:CANCELORDER:%s", orderNo);
    }

    public static String hwCompletedHotelOrderJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:HWCOMPLETEDHOTELORDER:%s:%s", orderNo, scheduleId);
    }

    public static String hwCompletedHotelOrderTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:HWCOMPLETEDHOTELORDER:%s:%s", orderNo, scheduleId);
    }

    public static String hwCompletedHotelOrderGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:HWCOMPLETEDHOTELORDER:%s", orderNo);
    }

    public static String hwRoom15MinSendVoiceJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:HWROOM15MINSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String hwRoom15MinSendVoiceTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:HWROOM15MINSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String hwRoom15MinSendVoiceGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:HWROOM15MINSENDVOICE:%s", orderNo);
    }


    public static String hwRoom5MinSendVoiceJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:HWROOM5MINSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String hwRoom5MinSendVoiceTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:HWROOM5MINSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String hwRoom5MinSendVoiceGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:HWROOM5MINSENDVOICE:%s", orderNo);
    }

    public static String lastReminderSendVoiceJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:LASTREMINDERSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String lastReminderSendVoiceTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:LASTREMINDERSENDVOICE:%s:%s", orderNo, scheduleId);
    }

    public static String lastReminderSendVoiceGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:LASTREMINDERSENDVOICE:%s", orderNo);
    }

    public static String closeDevicesJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:CLOSEDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String closeDevicesTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:CLOSEDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String closeDevicesGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:CLOSEDEVICES:%s", orderNo);
    }

    public static String openDevicesJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:OPENDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String openDevicesTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:OPENDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String openDevicesGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:OPENDEVICES:%s", orderNo);
    }

    public static String openContinuationOrdersDevicesJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:OPENCONTINUATIONORDERSDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String openContinuationOrdersDevicesTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:OPENCONTINUATIONORDERSDEVICES:%s:%s", orderNo, scheduleId);
    }

    public static String openContinuationOrdersDevicesGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:OPENCONTINUATIONORDERSDEVICES:%s", orderNo);
    }

    public static String createRoomServiceJobName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "JOB:ORDERROOMSERVICE:%s:%s", orderNo, scheduleId);
    }

    public static String createRoomServiceTriggerName(String orderNo, String scheduleId) {
        return String.format(Locale.getDefault(), "TRIGGER:ORDERROOMSERVICE:%s:%s", orderNo, scheduleId);
    }

    public static String createRoomServiceGroupName(String orderNo) {
        return String.format(Locale.getDefault(), "GROUP:ORDERROOMSERVICE:%s", orderNo);
    }

    private static String daysOfTheWeek(List<Integer> daysOfWeek) {
        return daysOfWeek.stream()
                .filter(it -> it >= SUN && it <= SAT)
                .distinct()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static String cronExpression(List<Integer> daysOfWeek, int hour, int minus) {
        final String days = daysOfTheWeek(daysOfWeek);
        return "0 " + minus + " " + hour + " ? * " + days;
    }

    private static String cronExpression(int year, int month, int dayOfMonth, int hour, int minus) {
        return "0 " + minus + " " + hour + " " + dayOfMonth + " " + month + " ? " + year;
    }

    public static ScheduleBuilder<?> scheduleBuilder(List<Integer> daysOfWeek, int year, int month, int day, int hour, int minus) {
        if (CollectionUtils.isEmpty(daysOfWeek)) {
            return CronScheduleBuilder.cronSchedule(cronExpression(year, month, day, hour, minus))
                    .withMisfireHandlingInstructionDoNothing();
        } else {
            return CronScheduleBuilder.cronSchedule(cronExpression(daysOfWeek, hour, minus))
                    .withMisfireHandlingInstructionDoNothing();
        }
    }
}

package com.lj.iot.fegin.job;

import com.lj.iot.biz.base.dto.*;
import com.lj.iot.common.base.dto.HwScheduleParamDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "job")
public interface JobFeignClient {

    @PostMapping(value = "inner/job/scene/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteSceneJob(@RequestBody IdDto dto);

    @PostMapping(value = "inner/job/scene/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> saveSceneJob(@RequestBody SceneJobParamDto dto);

    @PostMapping(value = "inner/job/schedule/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> addSchedule(@RequestBody ScheduleParamDto dto);

    @PostMapping(value = "inner/job/schedule/delete/device_id", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteScheduleByDeviceId(@RequestBody IdStrDto dto);

    @PostMapping(value = "inner/job/schedule/delete/schedule_id", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteScheduleByScheduleId(@RequestBody ScheduleParamDto dto);

    @PostMapping(value = "inner/job/clock/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> addClock(@RequestBody ScheduleParamDto dto);

    @PostMapping(value = "inner/job/clock/delete/device_id", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteClockByDeviceId(@RequestBody IdStrDto dto);

    @PostMapping(value = "inner/job/clock/delete/schedule_id", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteClockByScheduleId(@RequestBody ScheduleParamDto dto);

//    @PostMapping("inner/job/topic/save")
//    CommonResultVo<String> saveTopicJob(@RequestBody TopicJobParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/cancel_order_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> cancelOrderJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/completed_hotel_order_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> completedHotelOrderJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/room_15_min_send_voice_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> room15MinSendVoiceJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/room_5_min_send_voice_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> room5MinSendVoiceJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/cancel_order/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteHotelWechatCancelOrderJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/last_reminder_send_voice_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> lastReminderSendVoiceJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/hotel_wechat/close_devices_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> closeDevicesJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/order/deletes", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteHotelWechatOrderJobs(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/order/open_devices_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> openDevicesJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/order/open_continuation_orders_devices_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> openContinuationOrdersDevicesJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/room_service/create_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> createRoomServiceJob(@RequestBody HwScheduleParamDto dto);

    @PostMapping(value = "inner/job/hotel_wechat/room_service/delete_create_job", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResultVo<String> deleteRoomServiceJob(@RequestBody HwScheduleParamDto dto);
}
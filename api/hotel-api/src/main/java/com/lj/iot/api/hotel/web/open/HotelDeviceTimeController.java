package com.lj.iot.api.hotel.web.open;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.HotelDeviceTime;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelDeviceTimeService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * <p>
 * 设备续费时间 前端控制器
 * </p>
 *
 * @author xm
 * @since 2023-04-08
 */
@RequestMapping("api/open")
@RestController
public class HotelDeviceTimeController {
    @Resource
    private IHotelDeviceTimeService hotelDeviceTimeService;
    @Resource
    IUserDeviceService userDeviceService;

    /**
     * 新增酒店时间
     *
     * @return
     */
    @PostMapping("/set_device_time")
    public CommonResultVo<Boolean> setDeviceTime(@RequestBody @Valid HotelDeviceTime hotelDeviceTime) {
        hotelDeviceTimeService.saveOrUpdate(hotelDeviceTime);
        UserDevice one = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(hotelDeviceTime.getId()).build()));
        Duration duration = Duration.between(LocalDateTime.now(),hotelDeviceTime.getDatetime());
        hotelDeviceTime.setLongDatetime(duration.toSeconds());
        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, one.getProductId(), hotelDeviceTime.getId());
        MQTT.publish(topic, JSON.toJSONString(hotelDeviceTime));
        return CommonResultVo.SUCCESS();
    }



    /**
     * 新增酒店时间
     *
     * @return
     */
    @GetMapping("/find_device_time")
    public CommonResultVo<HotelDeviceTime> findDeviceTime(HotelDeviceTime hotelDeviceTime) {
        HotelDeviceTime one = hotelDeviceTimeService.getById(hotelDeviceTime.getId());
        if(one==null){
            return CommonResultVo.SUCCESS(one);
        }
        Duration duration = Duration.between(LocalDateTime.now(),one.getDatetime());
        one.setLongDatetime(duration.toSeconds());
        return CommonResultVo.SUCCESS(one);
    }
}

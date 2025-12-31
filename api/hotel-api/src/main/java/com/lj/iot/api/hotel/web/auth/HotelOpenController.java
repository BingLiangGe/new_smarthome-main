package com.lj.iot.api.hotel.web.auth;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.lj.iot.biz.db.smart.entity.HotelOpen;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IHotelOpenService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xm
 * @since 2023-05-16
 */
@Controller
@RequestMapping("api/auth/menu/hotelOpen")
public class HotelOpenController {
    @Autowired
    IHotelOpenService hotelOpenService;
    @Autowired
    IUserDeviceService userDeviceService;

    /**
     * 操作开房
     * @return
     */
    @PostMapping("option")
    public CommonResultVo<Boolean> option(@RequestBody HotelOpen hotelOpen) {
        hotelOpen.setOptionUser(UserDto.getUser().getActualUserId());
        CommonResultVo<Boolean> success = CommonResultVo.SUCCESS(hotelOpenService.updateById(hotelOpen));
        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(hotelOpen.getHomeId())
                .signalType("MASTER")
                .build()));
        for (UserDevice userDevice:
        list) {
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), userDevice.getDeviceId());
            userDeviceService.saveOrUpdate(UserDevice.builder()
                    .deviceId(userDevice.getDeviceId())
                            .isDel(hotelOpen.getOpenStatus()==1?true:false)
                    .build());
            MQTT.publish(topic, JSON.toJSONString(new HashMap(){{
                put("id",userDevice.getDeviceId());
                put("enable",hotelOpen.getOpenStatus());
            }}));
        }


        return success;
    }

}

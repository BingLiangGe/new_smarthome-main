package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IHotelFloorHomeService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.BizHomeUserService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/18
 * @since 1.0.0
 */
@Slf4j
@Component
public class BizWsPublishServiceImpl implements BizWsPublishService {

    @Autowired
    private BizHomeUserService bizHomeUserService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Async
    @Override
    public void publish(WsResultVo wsResultVo) {
        wsFeignClient.appSend(wsResultVo);

        //酒店消息处理  如果用户是酒店类型的账号，就往homeId对应酒店的所有账号上面推送消息
        hotelPublish(wsResultVo);
    }

    @Override
    public void publishAllMemeberFailure(String topic, String msg) {
        publish(WsResultVo.FAILURE(Lists.newArrayList(), null, topic, null, msg));
    }

    private void hotelPublish(WsResultVo wsResultVo) {

        log.info("hotelPublish wsResultVo={}",wsResultVo);
        List<String> hotelUserIds = new ArrayList<>();
        for (String userId : wsResultVo.getUserIds()) {
            UserAccount userAccount = userAccountService.getByIdCache(userId);
            if (userAccount != null) {
                if (AccountTypeEnum.HOTEL.getCode().equals(userAccount.getType())) {
                    HotelFloorHome hotelFloorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                            .homeId(wsResultVo.getHomeId())
                            .hotelUserId(userAccount.getId())
                            .build()));
                    if (hotelFloorHome != null) {
                        List<HotelUser> hotelUserList = hotelUserService.list(new QueryWrapper<>(HotelUser.builder()
                                .hotelId(hotelFloorHome.getHotelId())
                                .build()));
                        for (HotelUser hotelUser : hotelUserList) {
                            hotelUserIds.add(hotelUser.getMemberUserId());
                        }
                    }
                }
            }
        }
        if (hotelUserIds.size() > 0) {
            wsFeignClient.hotelSend(WsResultVo.builder()
                    .channel(wsResultVo.getChannel())
                    .userIds(hotelUserIds)
                    .homeId(wsResultVo.getHomeId())
                    .code(wsResultVo.getCode())
                    .msg(wsResultVo.getMsg())
                    .data(wsResultVo.getData())
                    .build());
        }
    }

    /**
     * 射频学码数据上报
     * {
     * "id": "123",  //消息ID
     * "code": 0,    //0:成功  -1:失败
     * "data":{
     * "signalType":"IR", //信号类型  IR\|RF
     * "keyId": 12,  //按键ID
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * "value": [89,23,23,...] //学到的码值
     * },
     * "msg": "success",   //消息描述
     * }
     *
     * @param topic
     */
    @Async
    @Override
    public void publishAllMemberByHomeId(String topic, Long homeId, Object data) {
        publish(WsResultVo.SUCCESS(
                bizHomeUserService.getMemberUserIdsByHomeId(homeId),
                homeId,
                topic,
                data));
    }

    @Override
    public void publishEditMemberByHomeId(String topic, Long homeId, Object data) {
        publish(WsResultVo.SUCCESS(
                bizHomeUserService.getEditMemberUserIdsByHomeId(homeId),
                homeId,
                topic,
                data));
    }

    @Override
    public void publishAllMemberByHomeIdFailure(String topic, Long homeId, Object data) {
        publish(WsResultVo.FAILURE(
                bizHomeUserService.getMemberUserIdsByHomeId(homeId),
                homeId,
                topic,
                data));
    }

    @Override
    public void publishEditMemberByHomeIdFailure(String topic, Long homeId, Object data) {
        publish(WsResultVo.FAILURE(
                bizHomeUserService.getEditMemberUserIdsByHomeId(homeId),
                homeId,
                topic,
                data));
    }

    @Override
    public void publishEditMemberByHomeIdFailure(String topic, Long homeId, Object data, String msg) {
        publish(WsResultVo.FAILURE(
                bizHomeUserService.getEditMemberUserIdsByHomeId(homeId),
                homeId,
                topic,
                data, msg));
    }


    @Override
    public void publishDevice(String topic, Object data) {
        publish(WsResultVo.SUCCESS(topic,
                data));
    }
}

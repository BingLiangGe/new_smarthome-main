package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IHotelFloorHomeService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.BizHomeUserService;
import com.lj.iot.biz.service.BizWatchPublishService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.fegin.watch.WatchFeignClient;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/18
 * @since 1.0.0
 */
@Slf4j
@Component
public class BizWatchPublishServiceImpl implements BizWatchPublishService {

    @Resource
    private WatchFeignClient watchFeignClient;

    @Async
    @Override
    public void publish(WatchMsgDto watchMsgDto) {
        log.info("send watch deviceId={},data={}", watchMsgDto.getDeviceId(), watchMsgDto.getData());
        watchFeignClient.watchSendMsg(watchMsgDto);
    }
}

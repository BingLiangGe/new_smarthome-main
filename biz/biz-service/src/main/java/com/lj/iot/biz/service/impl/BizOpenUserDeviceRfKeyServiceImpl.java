package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户设备按键
 *
 * @author mz
 * @Date 2023/2/6
 * @since 1.0.0
 */
@Slf4j
@Service
public class BizOpenUserDeviceRfKeyServiceImpl implements BizUserDeviceRfKeyService {
    @Autowired
    IUserDeviceRfKeyService userDeviceRfKeyService;

    @Autowired
    IUserDeviceService userDeviceService;

    @Override
    public List<UserDeviceRfKey> OfflineList(String masterDeviceId, String deviceId) {
        UserDevice masterDevice = userDeviceService.findDeviceByDeviceIdAndRoomId(masterDeviceId);
        ValidUtils.isNullThrow(masterDevice, "设备数据不存在");
        return userDeviceRfKeyService.listByCondition(masterDevice.getHomeId(),deviceId);
    }
}

package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.UserDeviceModeDto;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.db.smart.entity.UserDeviceMode;
import com.lj.iot.biz.db.smart.service.IUserDeviceModeService;
import com.lj.iot.biz.service.BizUploadEntityService;
import com.lj.iot.biz.service.BizUserDeviceModeService;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class BizUserDeviceModeServiceImpl implements BizUserDeviceModeService {

    @Resource
    IUserDeviceModeService userDeviceModeService;

    @Resource
    BizUploadEntityService bizUploadEntityService;

    @Override
    public UserDeviceMode edit(UserDeviceModeDto dto, String userId) {
        //获取模型数据
        UserDeviceMode userDeviceMode = userDeviceModeService
                .getOne(new QueryWrapper<>(UserDeviceMode.builder()
                        .id(dto.getId())
                        .userId(userId)
                        .build()));

        ValidUtils.isNullThrow(userDeviceMode, "数据不存在");

        userDeviceMode.setModeName(dto.getModeName());
        userDeviceMode.setThingModel(dto.getThingModel());
        userDeviceModeService.saveOrUpdate(userDeviceMode);
        //模式ServiceClockReplyTopicHandler
        bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Model);

        return userDeviceModeService.getById(userDeviceMode.getId());
    }
}

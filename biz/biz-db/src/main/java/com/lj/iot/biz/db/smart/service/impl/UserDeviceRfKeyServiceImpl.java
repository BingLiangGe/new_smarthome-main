package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.mapper.UserDeviceRfKeyMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户射频设备按键码值表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-30
 */
@DS("smart")
@Service
public class UserDeviceRfKeyServiceImpl extends ServiceImpl<UserDeviceRfKeyMapper, UserDeviceRfKey> implements IUserDeviceRfKeyService {
    @Override
    public UserDeviceRfKey findByDeviceIdAndCode(String deviceId, String code) {
        return getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .deviceId(deviceId)
                .keyCode(code).build()));
    }

    @Override
    public List<UserDeviceRfKey> listByCondition(Long homeId, String deviceId) {
        return this.baseMapper.listByCondition(homeId, deviceId);
    }


    @Override
    public List<UserDeviceRfKey> findByid(String deviceId) {
        return this.baseMapper.findByid(deviceId);
    }
}

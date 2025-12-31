package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户射频设备按键码值表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-30
 */
public interface IUserDeviceRfKeyService extends IService<UserDeviceRfKey> {

    UserDeviceRfKey findByDeviceIdAndCode(String deviceId,String code);

    List<UserDeviceRfKey> listByCondition(Long homeId, String deviceId);

    List<UserDeviceRfKey> findByid(String deviceId);
}

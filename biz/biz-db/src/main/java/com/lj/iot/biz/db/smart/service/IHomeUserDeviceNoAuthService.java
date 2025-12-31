package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.HomeUserDeviceNoAuth;

public interface IHomeUserDeviceNoAuthService extends IService<HomeUserDeviceNoAuth> {
    Boolean deleteAuth(Long homeUserId,Integer authType,String ids);
}

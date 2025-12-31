package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.HomeUserDeviceNoAuth;
import com.lj.iot.biz.db.smart.mapper.HomeUserDeviceNoAuthMapper;
import com.lj.iot.biz.db.smart.service.IHomeUserDeviceNoAuthService;
import org.springframework.stereotype.Service;

@DS("smart")
@Service
public class IHomeUserDeviceNoAuthServiceImpl extends ServiceImpl<HomeUserDeviceNoAuthMapper, HomeUserDeviceNoAuth> implements IHomeUserDeviceNoAuthService {
    @Override
    public Boolean deleteAuth(Long homeUserId, Integer authType, String ids) {
        return this.baseMapper.deleteAuth(homeUserId,authType,ids);
    }
}

package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.HomeUserDeviceNoAuth;

public interface HomeUserDeviceNoAuthMapper extends BaseMapper<HomeUserDeviceNoAuth> {
    Boolean deleteAuth(Long homeUserId, Integer authType, String ids);
}

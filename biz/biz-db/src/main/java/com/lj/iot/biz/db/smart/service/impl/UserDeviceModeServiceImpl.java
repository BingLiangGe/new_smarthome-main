package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.UserDeviceMode;
import com.lj.iot.biz.db.smart.mapper.UserDeviceModeMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceModeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户设备模式 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
@DS("smart")
@Service
public class UserDeviceModeServiceImpl extends ServiceImpl<UserDeviceModeMapper, UserDeviceMode> implements IUserDeviceModeService {

}

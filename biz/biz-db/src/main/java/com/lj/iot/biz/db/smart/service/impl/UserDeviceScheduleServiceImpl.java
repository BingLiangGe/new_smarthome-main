package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import com.lj.iot.biz.db.smart.mapper.UserDeviceScheduleMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备调度表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
@DS("smart")
@Service
public class UserDeviceScheduleServiceImpl extends ServiceImpl<UserDeviceScheduleMapper, UserDeviceSchedule> implements IUserDeviceScheduleService {

    @Override
    public List<UserDeviceSchedule> listByMasterDeviceIdAndCronOrRemark(String masterDeviceId, String cron, String remark) {
        return this.baseMapper.listByMasterDeviceIdAndCronOrRemark(masterDeviceId, cron, remark);
    }
}

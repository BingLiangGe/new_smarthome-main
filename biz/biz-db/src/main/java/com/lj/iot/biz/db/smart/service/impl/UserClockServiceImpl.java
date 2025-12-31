package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.mapper.UserClockMapper;
import com.lj.iot.biz.db.smart.service.IUserClockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 闹钟 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
@DS("smart")
@Service
public class UserClockServiceImpl extends ServiceImpl<UserClockMapper, UserClock> implements IUserClockService {

    @Resource
    private UserClockMapper mapper;

    @Override
    public List<UserClock> selectClockByStatus(Integer status) {
        List<UserClock> list = mapper.selectClockByStatus(status);
        return list;
    }

    @Override
    public List<UserClock> listByMasterDeviceIdAndCronOrRemark(String masterDeviceId, String cron, String remark) {
        return this.baseMapper.listByMasterDeviceIdAndCronOrRemark(masterDeviceId, cron, remark);
    }
}

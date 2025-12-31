package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.UserClock;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 设备调度表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
public interface IUserDeviceScheduleService extends IService<UserDeviceSchedule> {

    List<UserDeviceSchedule> listByMasterDeviceIdAndCronOrRemark(String masterDeviceId, String cron, String remark);
}

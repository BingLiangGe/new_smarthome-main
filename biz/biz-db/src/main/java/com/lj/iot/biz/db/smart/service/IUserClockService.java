package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.UserClock;

import java.util.List;

/**
 * <p>
 * 闹钟 服务类
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
public interface IUserClockService extends IService<UserClock> {

    public List<UserClock> selectClockByStatus(Integer status);

    List<UserClock> listByMasterDeviceIdAndCronOrRemark(String masterDeviceId, String cron, String remark);
}

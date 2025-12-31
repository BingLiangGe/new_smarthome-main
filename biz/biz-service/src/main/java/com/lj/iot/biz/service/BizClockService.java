package com.lj.iot.biz.service;

import com.lj.iot.biz.db.smart.entity.UserClock;

import java.util.List;

/**
 * 闹钟
 */
public interface BizClockService {

    /**
     * 触发闹钟
     *
     * @param
     */
    void trigger(UserClock clock);

    void deleteByDeviceId(String deviceId);

    void deleteByDeviceIdAndCron(String deviceId, String cron, String remark);

    List<UserClock> listByMasterDeviceId(String masterDeviceId);

    void delete(UserClock userClock);

    void delete(List<UserClock> userClockList);
}

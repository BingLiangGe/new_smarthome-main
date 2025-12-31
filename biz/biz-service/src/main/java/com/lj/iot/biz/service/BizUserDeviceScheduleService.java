package com.lj.iot.biz.service;

import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;

import java.util.List;

/**
 * 触发设备调度
 */
public interface BizUserDeviceScheduleService {

    /**
     * 触发设备调度
     *
     * @param
     */
    void trigger(UserDeviceSchedule userDeviceSchedule);


    List<UserDeviceSchedule> listByMasterDeviceId(String masterDeviceId);

    List<UserDeviceSchedule> listByDeviceId(String deviceId);

    /**
     * 根据设备号删除调度
     *
     * @param deviceId
     */
    void deleteByDeviceId(String deviceId);

    void delete(UserDeviceSchedule userDeviceSchedule);

    void delete(List<UserDeviceSchedule> userDeviceScheduleList);


    void deleteByDeviceIdAndCron(String deviceId, String cron, String remark);
}

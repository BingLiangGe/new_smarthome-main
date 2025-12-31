package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.UserDeviceSchedule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 设备调度表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
public interface UserDeviceScheduleMapper extends BaseMapper<UserDeviceSchedule> {

    List<UserDeviceSchedule> listByMasterDeviceIdAndCronOrRemark(@Param("masterDeviceId") String masterDeviceId,
                                                                 @Param("cron") String cron,
                                                                 @Param("remark") String remark);
}
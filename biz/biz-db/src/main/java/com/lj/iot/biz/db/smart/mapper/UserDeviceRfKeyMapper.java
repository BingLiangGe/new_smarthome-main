package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户射频设备按键码值表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-30
 */
public interface UserDeviceRfKeyMapper extends BaseMapper<UserDeviceRfKey> {

    List<UserDeviceRfKey> listByCondition(@Param("homeId") Long homeId, @Param("deviceId") String deviceId);

    List<UserDeviceRfKey> findByid(@Param("deviceId") String deviceId);
}

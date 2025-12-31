package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.UserClock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 闹钟 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-11-23
 */
public interface UserClockMapper extends BaseMapper<UserClock> {

    @Select("SELECT uc.`id`,uc.`user_id`,uc.`master_device_id`,uc.`cron`,uc.`remark`,uc.`create_time`,uc.`setting_time`,uc.`clock_status`,uc.`setting_time` FROM user_clock uc WHERE clock_status=#{status} AND setting_time <=NOW();")
    public List<UserClock> selectClockByStatus(Integer status);

    List<UserClock> listByMasterDeviceIdAndCronOrRemark(@Param("masterDeviceId") String masterDeviceId,
                                                        @Param("cron") String cron,
                                                        @Param("remark") String remark);
}

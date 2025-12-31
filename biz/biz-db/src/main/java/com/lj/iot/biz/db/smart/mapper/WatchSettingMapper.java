package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.base.vo.WatchSettingInfoVo;
import com.lj.iot.biz.db.smart.entity.WatchSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
public interface WatchSettingMapper extends BaseMapper<WatchSetting> {


    @Select("SELECT\n" +
            "  (SELECT setting_value FROM watch_setting WHERE device_id=ws.`device_id` AND data_type=ws.`data_type` AND setting_type=0) timeValue,\n" +
            "  (SELECT setting_value FROM watch_setting WHERE device_id=ws.`device_id` AND data_type=ws.`data_type` AND setting_type=1 AND value_type=0) highData,\n" +
            "  (SELECT setting_value FROM watch_setting WHERE device_id=ws.`device_id` AND data_type=ws.`data_type` AND setting_type=1 AND value_type=1) lowData\n" +
            "FROM\n" +
            "  watch_setting ws\n" +
            "WHERE ws.`device_id` = #{deviceId} AND ws.data_type=#{dataType} LIMIT 1;")
     WatchSettingInfoVo getWathSettingByDeviceIdAndType(String deviceId, Integer dataType);
}

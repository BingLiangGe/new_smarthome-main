package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * <p>
 * ota升级记录 Mapper 接口
 * </p>
 *
 * @author tyj
 */
public interface UpgradeRecordMapper extends BaseMapper<UpgradeRecord> {

    /**
     * 查询未成功ota升级内容
     * @param deviceId
     * @param softWareVersion
     * @return
     */
     UpgradeRecord findUpgradeRecordByNotSuccess(@Param("deviceId") String deviceId,@Param("softWareVersion") String softWareVersion,@Param("hardWareVersion") String hardWareVersion);
}

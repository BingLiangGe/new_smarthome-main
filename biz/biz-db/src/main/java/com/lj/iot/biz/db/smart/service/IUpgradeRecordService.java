package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;

import java.util.ArrayList;

/**
 * ota 升级记录
 * @author tyj
 */
public interface IUpgradeRecordService extends IService<UpgradeRecord> {


    public UpgradeRecord findUpgradeRecordByNotSuccess(String deviceId,String softWareVersion,String hardwareversion);
}

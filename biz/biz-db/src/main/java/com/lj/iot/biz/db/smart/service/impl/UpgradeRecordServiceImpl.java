package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import com.lj.iot.biz.db.smart.mapper.OperationLogMapper;
import com.lj.iot.biz.db.smart.mapper.UpgradeRecordMapper;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * <p>
 * oat升级 服务实现类
 * </p>
 *
 * @author tyj
 */
@DS("smart")
@Service
public class UpgradeRecordServiceImpl extends ServiceImpl<UpgradeRecordMapper, UpgradeRecord> implements IUpgradeRecordService {

    @Resource
    private UpgradeRecordMapper mapper;

    @Override
    public UpgradeRecord findUpgradeRecordByNotSuccess(String deviceId, String softWareVersion,String hardwareversion) {
        return mapper.findUpgradeRecordByNotSuccess(deviceId,softWareVersion,hardwareversion);
    }
}

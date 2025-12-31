package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.mapper.OperationLogMapper;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * <p>
 * 用户设备日志表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-26
 */
@DS("smart")
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Override
    public com.lj.iot.common.util.util.PageUtil<OperationLog> operationLogLimit(Integer pageIndex, Integer pageSize, OperationLog log) {
        com.lj.iot.common.util.util.PageUtil<OperationLog> page =new com.lj.iot.common.util.util.PageUtil<OperationLog>();

        page.setRows(operationLogMapper.selectOperationLogLimit(pageIndex,pageSize,log));
        page.setTotal(operationLogMapper.selectOperationLogLimitCount(log));
        return page;
    }

    @Override
    public void deleteOperationLogTask() {
        //operationLogMapper.deleteOperationLogTask();
    }

    @Override
    public IPage<OperationLog> customPage(DeviceIdPage2Dto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public ArrayList<OperationLog> task() {
        return this.baseMapper.task();
    }
}

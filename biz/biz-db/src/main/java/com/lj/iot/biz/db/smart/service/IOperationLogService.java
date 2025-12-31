package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.util.PageUtil;

import java.util.ArrayList;

/**
 * <p>
 * 用户设备日志表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-26
 */
public interface IOperationLogService extends IService<OperationLog> {


    PageUtil<OperationLog> operationLogLimit(Integer pageIndex, Integer pageSize, OperationLog log);

    void deleteOperationLogTask();

    IPage<OperationLog> customPage(DeviceIdPage2Dto pageDto);

    ArrayList<OperationLog> task();
}

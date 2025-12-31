package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户设备日志表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-26
 */
public interface OperationLogMapper extends BaseMapper<OperationLog> {


    Integer selectOperationLogLimitCount(@Param("log") OperationLog log);

    List<OperationLog> selectOperationLogLimit(@Param("pageIndex") Integer pageIndex, @Param("pageSize") Integer pageSize, @Param("log") OperationLog log);

    @Select("DELETE FROM operation_log WHERE create_time &lt; NOW() - INTERVAL 3 HOUR")
    void deleteOperationLogTask();


    IPage<OperationLog> customPage(IPage<OperationLog> page, @Param("params") DeviceIdPage2Dto pageDto);

    ArrayList<OperationLog> task();
}

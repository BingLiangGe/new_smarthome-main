package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DeviceIdPage2Dto;
import com.lj.iot.biz.db.smart.entity.OperationLog;
import com.lj.iot.biz.db.smart.service.IOperationLogService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备日志
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/operation/log")
public class AuthOperationLogController {

    @Autowired
    private IOperationLogService operationLogService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("operation:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<OperationLog>> page(DeviceIdPage2Dto pageDto) {
        return CommonResultVo.SUCCESS(operationLogService.customPage(pageDto));
    }
}

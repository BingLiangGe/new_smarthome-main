package com.lj.iot.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.system.entity.SysLog;
import com.lj.iot.common.system.service.ISysLogService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志
 *
 * @author xm
 * @since 2022-10-10
 */
@RestController
@RequestMapping("api/auth/sys/log")
public class SysLogController {

    @Autowired
    private ISysLogService sysLogService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @CustomPermissions("sys:log:list")
    public CommonResultVo<IPage<SysLog>> list(@RequestParam PageDto pageDto) {

        IPage<SysLog> page = sysLogService.page(PageUtil.page(pageDto),
                new QueryWrapper<>(SysLog.builder()
                        .build())
                        .like("username", pageDto.getSearch())
                        .like("operation", pageDto.getSearch()));

        return CommonResultVo.SUCCESS(page);
    }
}

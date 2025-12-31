package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.DeviceUpgradeLog;
import com.lj.iot.biz.db.smart.service.IDeviceUpgradeLogService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备升级管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/device_upgrade")
public class AuthDeviceUpgradeLogController {

    @Autowired
    private IDeviceUpgradeLogService deviceUpgradeLogService;

    /**
     * 设备升级日志
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("device:upgrade:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<DeviceUpgradeLog>> productPage(ProductPageDto pageDto) {
        return CommonResultVo.SUCCESS(deviceUpgradeLogService.customPage(pageDto));
    }
}

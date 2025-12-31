package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.IrDeviceType;
import com.lj.iot.biz.service.BizIrDeviceTypeService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红外设备类型管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/ir_device_type")
public class AuthIrDeviceTypeController {

    @Autowired
    private BizIrDeviceTypeService bizIrDeviceTypeService;

    /**
     * 分页
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("rf_device_type:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<IrDeviceType>> page(PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizIrDeviceTypeService.customPage(pageDto));
    }
}

package com.lj.iot.api.system.web.auth;

import com.lj.iot.biz.base.dto.DeviceTypeIdDto;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.service.BizRfBrandTypeService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 射频品牌类型管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/rf_brand_type")
public class AuthRFBrandTypeController {

    @Autowired
    private BizRfBrandTypeService bizRfBrandTypeService;

    /**
     * 列表(设备类型ID)
     *
     * @param idDto
     * @return
     */
    @CustomPermissions("rf_brand_type:list")
    @RequestMapping("/list")
    public CommonResultVo<List<RfBrandType>> list(@Valid DeviceTypeIdDto idDto) {
        return CommonResultVo.SUCCESS(bizRfBrandTypeService.listByDeviceTypeId(idDto.getDeviceTypeId()));
    }
}

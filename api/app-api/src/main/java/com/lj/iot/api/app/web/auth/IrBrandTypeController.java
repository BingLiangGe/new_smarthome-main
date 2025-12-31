package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.IrBrandTypeDto;
import com.lj.iot.biz.base.vo.BrandTypeVo;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.lj.iot.biz.db.smart.service.IIrBrandTypeService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 前端控制器
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("api/auth/ir_brand_type")
public class IrBrandTypeController {
    @Resource
    IIrBrandTypeService irBrandTypeService;

    /**
     * 查询红外设备品牌列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<BrandTypeVo>> list(@Valid IrBrandTypeDto dto) {
        return CommonResultVo.SUCCESS(irBrandTypeService.listByDeviceTypeId(dto.getDeviceTypeId()));
    }
}

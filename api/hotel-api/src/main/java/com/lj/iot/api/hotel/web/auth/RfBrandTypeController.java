package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.IrBrandTypeDto;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.lj.iot.biz.db.smart.service.IRfBrandTypeService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 射频设备品牌
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("api/auth/rf_brand_type")
public class RfBrandTypeController {
    @Resource
    IRfBrandTypeService brandTypeService;

    /**
     * 查询射频设备品牌列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<RfBrandType>> list(@Valid IrBrandTypeDto dto) {
        return CommonResultVo.SUCCESS(brandTypeService.list(new QueryWrapper<>(RfBrandType.builder()
                .deviceTypeId(dto.getDeviceTypeId()).build()).orderByAsc("first_letter")));
    }
}

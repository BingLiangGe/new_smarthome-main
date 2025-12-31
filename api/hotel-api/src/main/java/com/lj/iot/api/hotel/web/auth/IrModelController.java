package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.IrModelDto;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 红外模型
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/api/auth/ir_model")
public class IrModelController {
    @Resource
    IIrModelService irModelService;

    /**
     * 查询红外设备型号列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<IrModel>> list(@Valid IrModelDto dto) {
        return CommonResultVo.SUCCESS(irModelService.list(new QueryWrapper<>(IrModel.builder()
                .deviceTypeId(dto.getDeviceTypeId())
                .brandId(dto.getBrandId())
                .build())));
    }
}

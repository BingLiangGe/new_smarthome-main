package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.IrModelDto;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 射频型号
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/api/auth/rf_model")
public class RfModelController {
    @Resource
    IRfModelService modelService;

    /**
     * 查询红外设备型号列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<RfModel>> list(@Valid IrModelDto dto) {
        return CommonResultVo.SUCCESS(modelService.list(new QueryWrapper<>(RfModel.builder()
                .deviceTypeId(dto.getDeviceTypeId())
                .brandId(dto.getBrandId())
                .build())));
    }
}

package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.biz.base.dto.DeviceDto;
import com.lj.iot.biz.base.vo.ProductThingModelKeyVo;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 按键数据
 *
 * @author xm
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/api/auth/product_thing_model_key")
public class ProductThingModelKeyController {
    @Resource
    BizProductThingModelKeyService bizProductThingModelKeyService;

    /**
     * 产品按键数据
     *
     * @param dto
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<ProductThingModelKey>> list(@Valid DeviceDto dto) {
        return CommonResultVo.SUCCESS(bizProductThingModelKeyService.keyList(dto));
    }
}

package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.ProductIdDto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.service.IEntityAliasService;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 设备别名
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/entity_alias")
public class EntityAliasController {

    @Autowired
    private IEntityAliasService entityAliasService;
    @Autowired
    private IProductService productService;

    /**
     * 分页
     *
     * @return
     */
    @RequestMapping("/room/list")
    public CommonResultVo<List<EntityAlias>> room() {
        return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("room")
                .build())));
    }

    /**
     * 分页
     *
     * @param deviceType
     * @return
     */
    @RequestMapping("/device/list")
    public CommonResultVo<List<EntityAlias>> device(@RequestParam("deviceType") String deviceType, Integer isAll) {

        if (isAll != null && "light".equals(deviceType)) {
            return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                    .attrType("device")
                    .subDeviceType(deviceType)
                    .entityKey("1")
                    .build())));
        }

        return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("device")
                .subDeviceType(deviceType)
                .build())));
    }

    @RequestMapping("/secne/list")
    public CommonResultVo<List<EntityAlias>> scene() {
        return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("secne")
                .build())));
    }
}

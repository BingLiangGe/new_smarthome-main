package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.UserGoodsAddDto;
import com.lj.iot.biz.base.dto.UserGoodsEditDto;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.biz.service.BizUserGoodsService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 商品库存
 */
@RestController
@RequestMapping("api/auth/user_goods")
public class UserGoodsController {

    @Autowired
    private BizUserGoodsService bizUserGoodsService;

    /**
     * 分页
     */
    @RequestMapping("page")
    @CustomPermissions("user_goods:page")
    public CommonResultVo<IPage<UserGoods>> page(@Valid PageDto dto) {
        return CommonResultVo.SUCCESS(bizUserGoodsService.customPage(dto, UserDto.getUser().getHotelId(), UserDto.getUser().getActualUserId()));
    }


    @RequestMapping("pageDevice")
    public CommonResultVo<IPage<SeachDeviceVo>> pageDevice(@Valid PageDto dto) {
        return CommonResultVo.SUCCESS(bizUserGoodsService.customPageUserDevice(dto));
    }

    /**
     * 添加
     */
    @PostMapping("add")
    @CustomPermissions("user_goods:add")
    public CommonResultVo<String> add(@RequestBody @Valid UserGoodsAddDto dto) {
        bizUserGoodsService.add(dto, UserDto.getUser().getHotelId(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑
     */
    @PostMapping("edit")
    @CustomPermissions("user_goods:edit")
    public CommonResultVo<String> edit(@RequestBody @Valid UserGoodsEditDto dto) {
        bizUserGoodsService.edit(dto, UserDto.getUser().getHotelId(),UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    @CustomPermissions("user_goods:delete")
    public CommonResultVo deleteHomeById(@RequestBody @Valid IdDto dto) {
        bizUserGoodsService.delete(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }
}

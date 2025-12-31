package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeDirectDeviceDto;
import com.lj.iot.biz.db.smart.entity.HomeDirectDevice;
import com.lj.iot.biz.db.smart.service.IHomeDirectDeviceService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 *  直连设备前端控制器
 * </p>
 *
 * @author xm
 * @since 2022-12-30
 */
@RestController
@RequestMapping("/api/auth/homeDirectDevice")
public class HomeDirectDeviceController {

    @Resource
    IHomeDirectDeviceService homeDirectDeviceService;

    /**
     * 添加家庭
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("add")
    public CommonResultVo<HomeDirectDevice> add(@RequestBody HomeDirectDeviceDto dto) {
        return CommonResultVo.SUCCESS(homeDirectDeviceService.add(dto, UserDto.getUser().getUId()));
    }


    /**
     * 添加家庭
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("edit")
    public CommonResultVo<HomeDirectDevice> edit(@RequestBody HomeDirectDeviceDto dto) {
        return CommonResultVo.SUCCESS(homeDirectDeviceService.edit(dto, UserDto.getUser().getUId()));
    }

    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @GetMapping("{homeId}")
    public CommonResultVo<HomeDirectDevice> get(@PathVariable Long homeId) {
        return CommonResultVo.SUCCESS(homeDirectDeviceService.get(homeId));
    }
}

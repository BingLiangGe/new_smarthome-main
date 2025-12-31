package com.lj.iot.api.hotel.web.auth;


import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.UserDeviceModeDto;
import com.lj.iot.biz.service.BizUserDeviceModeService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 设备自定义模式修改
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("/api/auth/user_device_mode")
public class UserDeviceModeController {

    @Resource
    private BizUserDeviceModeService bizUserDeviceModeService;

    /**
     * 修改设备自定义模式数据
     *
     * @param dto
     * @return
     */
    @PostMapping("/edit")
    @CustomPermissions("user_device_mode:edit")
    public CommonResultVo edit(@RequestBody @Valid UserDeviceModeDto dto) {
        bizUserDeviceModeService.edit(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }
}

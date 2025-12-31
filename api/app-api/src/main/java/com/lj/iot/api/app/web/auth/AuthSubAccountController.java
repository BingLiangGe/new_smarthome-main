package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.SubDeviceAccountEditDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 子账号
 */
@RestController
@RequestMapping("/api/auth/sub/account")
public class AuthSubAccountController {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    /**
     * 切换账户类型[主控设备]
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.MAIN)
    @PostMapping("edit")
    public CommonResultVo<String> edit(@RequestBody @Valid SubDeviceAccountEditDto dto) {
        UserDevice userDevice = userDeviceService.masterStatus(dto.getDeviceId(), UserDto.getUser().getUId());
        UserAccount user = bizUserAccountService.editDeviceUserAccount(userDevice, dto.getEditFlag());

        //刷新token
        LoginUtils.fresh(UserDto.builder()
                .platform(PlatFormEnum.APP.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS();
    }
}

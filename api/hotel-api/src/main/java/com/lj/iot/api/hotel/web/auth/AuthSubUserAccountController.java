package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
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
@RequestMapping("/api/auth/sub/user_account")
public class AuthSubUserAccountController {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    /**
     * 切换账户类型[主控设备]
     *
     * @param dto
     * @return
     */
    @PostMapping("edit")
    @CustomPermissions("sub:user_account:edit")
    public CommonResultVo<String> edit(@RequestBody @Valid SubDeviceAccountEditDto dto) {

        UserDevice userDevice = userDeviceService.masterStatus(dto.getDeviceId(), UserDto.getUser().getActualUserId());
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

    /**
     * 新增子账号[酒店账号]
     *
     * @param dto
     * @return
     */
    @PostMapping("add")
    @CustomPermissions("sub:user_account:add")
    public CommonResultVo<LoginVo<UserAccount>> add(@RequestBody @Valid SubTempAccountAddDto dto) {
        UserAccount user = bizUserAccountService.addTempUserAccount(dto, UserDto.getUser().getActualUserId());

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.APP.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.<UserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .token(token).build());
    }

    /**
     * 刷新TOKEN[酒店账号]
     *
     * @param dto
     * @return
     */
    @PostMapping("fresh_token")
    @CustomPermissions("sub:user_account:fresh_token")
    public CommonResultVo<LoginVo<UserAccount>> freshToken(@RequestBody @Valid SubTempAccountEditDto dto) {

        UserAccount user = bizUserAccountService.freshTokenTempUserAccount(dto, UserDto.getUser().getActualUserId());

        //刷新token
        LoginUtils.fresh(UserDto.builder()
                .platform(PlatFormEnum.APP.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除子账号[酒店账号]
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    @CustomPermissions("sub:user_account:delete")
    public CommonResultVo<LoginVo<UserAccount>> delete(@RequestBody @Valid IdStrDto dto) {
        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                .id(dto.getId())
                .actualUserId(UserDto.getUser().getActualUserId())
                .type(AccountTypeEnum.HOTEL_SUB_TEMP.getCode())
                .build()));

        ValidUtils.isNullThrow(user, "数据不存在");

        bizUserAccountService.cancellation(user.getId());

        LoginUtils.logout(UserDto.builder()
                .platform(PlatFormEnum.APP.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS();
    }
}

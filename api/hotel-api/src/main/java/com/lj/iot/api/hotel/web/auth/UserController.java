package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.UserAccountEditDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户操作相关接口
 */
@RestController
@RequestMapping("api/auth/user")
public class UserController {

    @Resource
    private IUserAccountService userAccountService;

    /**
     * 个人详情
     *
     * @return
     */
    @RequestMapping("/info")
    @CustomPermissions("user:info")
    public CommonResultVo<UserAccount> info() {
        return CommonResultVo.SUCCESS(userAccountService.getById(UserDto.getUser().getActualUserId()));
    }

    /**
     * 修改用户信息
     *
     * @param dto
     * @return
     */
    @PostMapping("/edit")
    @CustomPermissions("user:edit")
    public CommonResultVo<UserAccount> edit(@RequestBody @Valid UserAccountEditDto dto) {
        return CommonResultVo.SUCCESS(userAccountService.edit(dto, UserDto.getUser().getActualUserId()));
    }
}

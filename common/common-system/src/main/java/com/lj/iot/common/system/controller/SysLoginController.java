/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.common.system.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.dto.SysLoginDto;
import com.lj.iot.common.system.entity.SysUser;
import com.lj.iot.common.system.service.ISysPasswordService;
import com.lj.iot.common.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 登录相关
 *
 * @author Mark sunlightcs@gmail.com
 */
@RequestMapping("api/open")
@RestController
public class SysLoginController {
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private ISysPasswordService sysPasswordService;

    /**
     * 验证码
     */
    @GetMapping("captcha.jpg")
    public void captcha(HttpServletResponse response, String uuid) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 50,4,5);
        //把验证码信息存到sesion
        cacheService.addSeconds("system:captcha:" + uuid, lineCaptcha.getCode(), 120);
        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 登录
     */
    @PostMapping("/sys/user/login")
    public CommonResultVo<LoginVo> login(@Valid SysLoginDto dto) {

        String code = cacheService.get("system:captcha:" + dto.getUuid());
       // ValidUtils.isFalseThrow(dto.getCaptcha().equals(code), "验证码不正确");

        //用户信息
        SysUser user = sysUserService.queryByUserName(dto.getUsername());

        //账号锁定
        if (user.getStatus()==0) {
            return CommonResultVo.FAILURE_MSG("账号已被锁定,请联系管理员");
        }

        sysPasswordService.check(user, dto.getPassword());

        String token = LoginUtils.login(UserDto.builder()
                .uId(user.getUserId()+"")
                .account(user.getUsername())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.builder()
                .account(user.getUsername())
                .token(token)
                .build());
    }


    /**
     * 退出
     */
    @PostMapping("/sys/logout")
    public CommonResultVo logout() {
        UserDto userDto = UserDto.getUser();
        if (userDto != null) {
            LoginUtils.logout(userDto);
        }
        return CommonResultVo.SUCCESS();
    }

}

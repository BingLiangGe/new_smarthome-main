package com.lj.iot.common.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.properties.SSOProperties;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.entity.SysUser;
import com.lj.iot.common.system.service.ISysPasswordService;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DS("system")
@Service
public class SysPasswordServiceImpl implements ISysPasswordService {

    @Autowired
    private ICacheService cacheService;
    @Autowired
    private SSOProperties ssoPropertiesDto;
    @Autowired
    private ISysUserService userService;

    @Override
    public void check(SysUser user, String password) {
        String key = ssoPropertiesDto.getApp() + RedisConstant.password_check + user.getUserId();
        Integer count = cacheService.get(key);
        count = count == null ? 1 : ++count;
        if (!MD5Utils.verify(password, user.getSalt(), user.getPassword())) {
            cacheService.addSeconds(key, count, 120);
            if (count == 4) {
                throw CommonException.FAILURE("密码错误4次，第5次账户将被禁用");
            }
            if (count >= 5) {
                LoginUtils.logout(UserDto.builder()
                        .account(user.getUsername())
                        .build());
                cacheService.del(key);
                userService.updateById(SysUser.builder()
                        .userId(user.getUserId())
                        .status(0)
                        .build());
                throw CommonException.FAILURE("密码错误到达5次，账户已被禁用");
            }
            throw CommonException.FAILURE("账号或密码错误");
        }
        cacheService.del(key);
    }
}

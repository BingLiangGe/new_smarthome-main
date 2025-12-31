package com.lj.iot.common.system.aop;

import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.util.ValidUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Aspect
public class PermissionsAop {

    @Autowired
    private ISysUserService sysUserService;

    @Around(value = "@annotation(customPermissions))")
    public Object customPermissions(ProceedingJoinPoint joinPoint, CustomPermissions customPermissions) throws Throwable {
        UserDto user = LoginUtils.getUser();
        ValidUtils.isNullThrow(user, CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录已过期，请重新登录");

        Set<String> stringSet = sysUserService.getUserPermissions(user.getLongId());
        //获取权限
        ValidUtils.isFalseThrow(user.getUId().equals("1") || stringSet.contains(customPermissions.value()),
                "权限不足");
        return joinPoint.proceed();
    }
}

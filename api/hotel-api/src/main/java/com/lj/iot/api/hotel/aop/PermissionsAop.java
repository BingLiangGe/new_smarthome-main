package com.lj.iot.api.hotel.aop;

import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class PermissionsAop {

    @Around(value = "@annotation(customPermissions))")
    public Object customPermissions(ProceedingJoinPoint joinPoint, CustomPermissions customPermissions) throws Throwable {
        UserDto user = LoginUtils.getUser();
        ValidUtils.isNullThrow(user, CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录已过期，请重新登录");

        //获取权限
        ValidUtils.isFalseThrow(user.getIsMain() || user.getPerms().contains(customPermissions.value()),
                "权限不足");
        return joinPoint.proceed();
    }
}

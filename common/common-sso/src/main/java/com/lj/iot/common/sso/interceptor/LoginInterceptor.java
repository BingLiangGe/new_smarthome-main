package com.lj.iot.common.sso.interceptor;


import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.properties.SSOProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements AsyncHandlerInterceptor {

    private SSOProperties ssoProperties;
    @Autowired
    private ICacheService cacheService;

    public LoginInterceptor(SSOProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录参数校验
        String accessToken = request.getHeader(ssoProperties.getTokenName());

        log.info("token={},",accessToken);

        if (!StringUtils.hasLength(accessToken)) {
            throw CommonException.INSTANCE(CodeConstant.LOGIN_INFO_NOT_EXIST, "您还未登录,请先登录");
        }

        UserDto user = cacheService.get(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_USER + accessToken);
        if (user == null) {
            throw CommonException.INSTANCE(CodeConstant.LOGIN_INFO_NOT_EXIST, "您还未登录,请先登录");
        }
        return true;
    }
}

package com.lj.iot.common.sso.interceptor;

import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.properties.SSOProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserDtoInterceptor implements AsyncHandlerInterceptor {

    private SSOProperties ssoProperties;
    @Autowired
    private ICacheService cacheService;

    public UserDtoInterceptor(SSOProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录参数
        String accessToken = request.getHeader(ssoProperties.getTokenName());

        if (!StringUtils.hasLength(accessToken)) {
            request.setAttribute("currentUser", null);
            return true;
        }

        //获取用户详情
        UserDto user = cacheService.get(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_USER + accessToken);
        if (user == null) {
            request.setAttribute("currentUser", null);
            return true;
        }
        // 刷新失效时间
        cacheService.addSeconds(ssoProperties.getApp() + RedisConstant.SESSION_ACCOUNT_2_TOKEN + user.getAccount(), accessToken, ssoProperties.getExpired());
        cacheService.addSeconds(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_ACCOUNT + accessToken, user.getAccount(), ssoProperties.getExpired());
        cacheService.addSeconds(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_USER + accessToken, user, ssoProperties.getExpired());
        request.setAttribute("currentUser", user);
        return true;
    }
}

package com.lj.iot.common.sso.interceptor;

import com.lj.iot.common.base.constant.CodeConstant;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.properties.SSOProperties;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultipointLoginInterceptor implements AsyncHandlerInterceptor {

    private SSOProperties ssoProperties;

    @Autowired
    private ICacheService cacheService;

    public MultipointLoginInterceptor(SSOProperties ssoProperties) {
        this.ssoProperties = ssoProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ValidUtils.isTrueThrow(request.getParameterMap().containsKey("uId") || request.getParameterMap().containsKey("googleCCCFDF")
                , "不能传入敏感参数");
      /*  // 登录参数校验
        String accessToken = request.getHeader(ssoProperties.getTokenName());
        if (StringUtils.isEmpty(accessToken)) {
            return true;
        }

        String mobile = cacheService.get(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_ACCOUNT + accessToken);

        if (mobile != null) {
            String loginToken = cacheService.get(ssoProperties.getApp() + RedisConstant.SESSION_ACCOUNT_2_TOKEN + mobile);
            if (loginToken != null && !loginToken.equals(accessToken)) {

                // 只会提醒一次
                cacheService.del(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_ACCOUNT + accessToken);
                cacheService.del(ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_USER + accessToken);
                throw CommonException.INSTANCE(CodeConstant.LOGIN_INFO_NOT_EXIST, "您已在其它设备登录,请重新登录");
            }
        }*/

        return true;
    }
}
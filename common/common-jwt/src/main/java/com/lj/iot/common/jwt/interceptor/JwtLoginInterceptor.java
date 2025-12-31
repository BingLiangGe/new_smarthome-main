package com.lj.iot.common.jwt.interceptor;


import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.jwt.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtLoginInterceptor implements AsyncHandlerInterceptor {

    private final String DEFAULT_TOKEN_NAME = "Authorization";
    private final String DEFAULT_JWT_CCCFDF_KEY = "123456";
    private final String DEFAULT_AES_CCCFDF_KEY = "123456";
    private final String tokenName;

    public JwtLoginInterceptor() {
        this.tokenName = this.DEFAULT_TOKEN_NAME;
    }

    public JwtLoginInterceptor(String tokenName, String jwtCCCFDFKey, String uidAesCCCFDFKey) {
        this.tokenName = !StringUtils.hasText(tokenName) ? this.DEFAULT_TOKEN_NAME : tokenName;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ValidUtils.isTrueThrow(request.getParameterMap().containsKey("uId") || request.getParameterMap().containsKey("googleCCCFDF")
                , "不能传入敏感参数");

        String token = request.getHeader(this.tokenName);
        if (StringUtils.hasLength(token)) {
            UserDto userDto = null;
            try {
                userDto = LoginUtils.verifyToken(token);
            } catch (SignatureVerificationException e) {
                log.error("JwtLoginInterceptor.preHandle.error={}", e.getMessage(), e);
                throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录信息被篡改！");
            } catch (TokenExpiredException e) {
                log.error("JwtLoginInterceptor.preHandle.error={}", e.getMessage(), e);
                throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录信息已过期！");
            } catch (Exception e) {
                log.error("JwtLoginInterceptor.preHandle.error={}", e.getMessage(), e);
                throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录信息解析异常！");
            }

            request.setAttribute("currentUser", userDto);
            return true;

        }

        log.error("JwtLoginInterceptor.preHandle.error={}", "登录信息不存在！");
        throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "登录信息不存在！");
    }
}


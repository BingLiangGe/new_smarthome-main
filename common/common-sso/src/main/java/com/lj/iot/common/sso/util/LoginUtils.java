package com.lj.iot.common.sso.util;

import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.properties.SSOProperties;
import com.lj.iot.common.util.IdUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class LoginUtils {

    @Resource
    private ICacheService cacheService;
    @Resource
    private SSOProperties ssoProperties;

    private static LoginUtils INSTANCE;

    public LoginUtils() {
        INSTANCE = this;
    }

    public static String login(UserDto userDto) {

       // if (!"18670715555".equals(userDto.getAccount()) && !"15580952238".equals(userDto.getAccount()) ){
            //去掉旧的toekn
            logout(userDto);
        //}

        //获取新的token
        String token = IdUtils.uuid();
        String platform = StringUtils.hasText(userDto.getPlatform()) ? userDto.getPlatform() : INSTANCE.ssoProperties.getApp();
        userDto.setPlatform(platform);
        INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount(), token);
        INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_TOKEN_2_ACCOUNT + token, userDto.getAccount());
        INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_TOKEN_2_USER + token, userDto);
        return token;
    }

    public static void fresh(UserDto userDto) {
        String platform = StringUtils.hasText(userDto.getPlatform()) ? userDto.getPlatform() : INSTANCE.ssoProperties.getApp();
        userDto.setPlatform(platform);
        String token = INSTANCE.cacheService.get(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount());
        if (StringUtils.hasLength(token)) {
            INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount(), token);
            INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_TOKEN_2_ACCOUNT + token, userDto.getAccount());
            INSTANCE.cacheService.setPermanent(platform + RedisConstant.SESSION_TOKEN_2_USER + token, userDto);
        }
    }


    public static UserDto getUser() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        String accessToken = request.getHeader(INSTANCE.ssoProperties.getTokenName());
        if (!StringUtils.hasLength(accessToken)) {
            return null;
        }
        return INSTANCE.cacheService.get(INSTANCE.ssoProperties.getApp() + RedisConstant.SESSION_TOKEN_2_USER + accessToken);
    }

    public static String getLoginUserId(){
        UserDto userDto = LoginUtils.getUser();
        if(userDto == null){
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
        return userDto.getUId();
    }

    public static void logout(UserDto userDto) {
        String platform = StringUtils.hasText(userDto.getPlatform()) ? userDto.getPlatform() : INSTANCE.ssoProperties.getApp();
        String token = INSTANCE.cacheService.get(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount());
        INSTANCE.cacheService.del(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount());
        if (StringUtils.hasLength(token)) {
            INSTANCE.cacheService.del(platform + RedisConstant.SESSION_TOKEN_2_ACCOUNT + token);
            INSTANCE.cacheService.del(platform + RedisConstant.SESSION_TOKEN_2_USER + token);
        }
    }


    public static String getToken(UserDto userDto) {
        String platform = StringUtils.hasText(userDto.getPlatform()) ? userDto.getPlatform() : INSTANCE.ssoProperties.getApp();
        String token = INSTANCE.cacheService.get(platform + RedisConstant.SESSION_ACCOUNT_2_TOKEN + userDto.getAccount());
       return token;
    }
}

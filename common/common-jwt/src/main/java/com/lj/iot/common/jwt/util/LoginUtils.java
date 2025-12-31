package com.lj.iot.common.jwt.util;


import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.jwt.properties.JwtProperties;
import com.lj.iot.common.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

import static com.auth0.jwt.JWT.create;
import static com.auth0.jwt.JWT.require;

@Slf4j
@Component
public class LoginUtils {


    private static JwtProperties jwtConfigDto;

    @Autowired
    private JwtProperties properties;


    @PostConstruct
    public void init() {
        jwtConfigDto = properties;
    }

    /**
     * 生成token
     *
     * @param userDto
     * @param
     * @return
     */
    public static String login(UserDto userDto) {
        Calendar current = Calendar.getInstance();
        Date now = current.getTime();
        //失效时间设置
        current.add(Calendar.MINUTE, jwtConfigDto.getExpired());
        Date exp = current.getTime();
        try {
            return create()
                    .withClaim("uid", AesUtils.aesEncrypt(userDto.getUId().toString(), jwtConfigDto.getUidAesCCCFDFKey()))
                    .withClaim("sub", mask(userDto.getAccount()))
                    .withClaim("jti", UUID.randomUUID().toString())
                    .withClaim("iat", now)
                    .withClaim("exp", exp)
                    .sign(Algorithm.HMAC256(jwtConfigDto.getJwtCCCFDFKey()));
        } catch (Exception e) {
            log.error("JwtUtils.createToken", e);
            throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "生成Token异常！");
        }
    }

    public static String mask(String phone) {
        if (!StringUtils.hasLength(phone) || phone.length() < 7) {
            return phone;
        }
        int midIdx = phone.length() / 2;
        int leftOffset = midIdx - 2;
        int rightOffset = midIdx + 2;
        String left = phone.substring(0, leftOffset);
        String right = phone.substring(rightOffset, phone.length());
        return left + "****" + right;
    }

    /**
     * 解析TOKEN
     *
     * @param token
     * @return
     */
    public static UserDto verifyToken(String token) {
        try {
            JWTVerifier verifier = require(Algorithm.HMAC256(jwtConfigDto.getJwtCCCFDFKey())).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            return UserDto.builder()
                    .uId(Objects.requireNonNull(AesUtils.aesDecrypt((claims.get("uid")).asString(), jwtConfigDto.getUidAesCCCFDFKey())))
                    .account((claims.get("sub")).asString())
                    .build();
        } catch (Exception e) {
            log.error("JwtUtils.verifyToken", e);
            throw CommonException.INSTANCE(CommonCodeEnum.LOGIN_INFO_NOT_EXIST.getCode(), "解析Token异常！");
        }
    }
}


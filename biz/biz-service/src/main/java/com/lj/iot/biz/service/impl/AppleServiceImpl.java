package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.dto.AppleLoginDTO;
import com.lj.iot.biz.base.enums.AccountTypeEnum;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.util.ValidUtils;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Objects;

/**
 * 苹果登录业务处理
 *
 * @author tyj
 */
@Service
@Slf4j
public class AppleServiceImpl {

    private String appleAuthUrl = "https://appleid.apple.com/auth/keys";

    private String appleIssUrl = "https://appleid.apple.com";




    /**
     * apple 登录
     * @param identityToken
     * @return
     */
    public String appleLogin(String identityToken) {
        // 获取秘钥的返回信息
        String firstDate = null;
        String claim = null;
        try {
            firstDate = new String(Base64.decodeBase64(identityToken.split("\\.")[0]), "UTF-8");
            claim = new String(Base64.decodeBase64(identityToken.split("\\.")[1]), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("apple授权码异常，identityToken[%s], %s", identityToken, e);
            ValidUtils.isNullThrow(e, "apple授权码异常，identityToken");
        }

        // 开发者帐户中获取的 10 个字符的标识符密钥
        String kid = JSONObject.parseObject(firstDate).get("kid").toString();
        String aud = JSONObject.parseObject(claim).get("aud").toString();
        String sub = JSONObject.parseObject(claim).get("sub").toString();

        PublicKey publicKey = this.getPublicKey(kid);
        if (Objects.isNull(publicKey)) {
            ValidUtils.isNullThrow(publicKey, "apple授权登录的数据异常");
        }

        String appleId = this.verifyAppleLoginCode(publicKey, identityToken, aud, sub);

        if (appleId != null) {
            return appleId;
        }
        return null;
    }

    private String verifyAppleLoginCode(PublicKey publicKey, String identityToken, String aud, String sub) {
        String apple_id=null;
        JwtParser jwtParser = Jwts.parser().setSigningKey(publicKey);
        jwtParser.requireIssuer(appleIssUrl);
        jwtParser.requireAudience(aud);
        jwtParser.requireSubject(sub);
        try {
            Jws<Claims> claim = jwtParser.parseClaimsJws(identityToken);
            if (claim != null && claim.getBody().containsKey("auth_time")) {
                apple_id=claim.getBody().getSubject();
            }
        } catch (ExpiredJwtException e) {
            ValidUtils.isNullThrow(e, "apple登录授权identityToken过期");
        } catch (SignatureException e) {
            ValidUtils.isNullThrow(e, "apple登录授权identityToken非法");
        }
        return apple_id;
    }

    private PublicKey getPublicKey(String kid) {
        try {
            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
            URIBuilder uriBuilder = new URIBuilder(appleAuthUrl);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            HttpResponse response = httpclient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity responseEntity = response.getEntity();
            String result = EntityUtils.toString(responseEntity, "UTF-8");
            if (statusCode != HttpStatus.SC_OK) { // code = 200
                ValidUtils.isNullThrow(statusCode, String.format("接口请求失败，url[%s], result[%s]", appleAuthUrl, result));
            }

            // 请求成功
            JSONObject content = JSONObject.parseObject(result);
            String keys = content.getString("keys");
            JSONArray keysArray = JSONObject.parseArray(keys);
            if (keysArray.isEmpty()) {
                return null;
            }

            for (Object key : keysArray) {
                JSONObject keyJsonObject = (JSONObject) key;
                if (kid.equals(keyJsonObject.getString("kid"))) {
                    String n = keyJsonObject.getString("n");
                    String e = keyJsonObject.getString("e");
                    BigInteger modulus = new BigInteger(1, Base64.decodeBase64(n));
                    BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(e));
                    RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    return kf.generatePublic(spec);
                }
            }
        } catch (Exception ex) {
            log.error("获取PublicKey异常.", ex);
        }
        return null;
    }

}

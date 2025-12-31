package com.lj.iot.common.pay.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wechat.pay.contrib.apache.httpclient.util.RsaCryptoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * 微信支付V3 工具类
 */
@Slf4j
public class WeChatPayV3 {

    private PrivateKey merchantPrivateKey;
    private CloseableHttpClient httpClient;
    private AutoUpdateCertificatesVerifier verifier;

    private String appId;
    private String mchId;
    private byte[] apiV3Key;
    private String unifiedOrderUrl;

    public WeChatPayV3(WeChatPayProperties properties) {
        if(StringUtils.isBlank(properties.getAppId())){
            log.error("=====================  该微服务不需要微信支付功能  ==================");
            return;
        }
        appId = properties.getAppId();
        mchId = properties.getMchId();
        apiV3Key = properties.getApiV3Key().getBytes(StandardCharsets.UTF_8);
        unifiedOrderUrl = properties.getUnifiedOrderUrl();

        try {
            //商户私钥证书
            ClassPathResource keyClassPathResource = new ClassPathResource(properties.getKeyPath());
            InputStream keyStream = keyClassPathResource.getInputStream();

            byte[] keyData = IOUtils.toByteArray(keyStream);
            keyStream.read(keyData);
            keyStream.close();
            // 加载商户私钥（privateKey：私钥字符串）
            merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(keyData));

            //商户私钥证书(cert)
            ClassPathResource certClassPathResource = new ClassPathResource(properties.getCertPath());
            InputStream certStream = certClassPathResource.getInputStream();
            // 商户证书
            X509Certificate certificate = PemUtil.loadCertificate(certStream);
            certStream.close();
            //获取证书序列号
            String mchSerialNo = certificate.getSerialNumber().toString(16).toUpperCase();

            // 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
            verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)), apiV3Key);

            // 初始化httpClient
            httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(verifier)).build();
        } catch (Exception e) {
            log.error("Error init.", e);
            throw CommonException.FAILURE("初始化异常");
        }
    }

    /**
     * app 统一下单
     *
     * @throws
     * @Title: unifiedOrder
     * @param: @param orderNo 订单编号
     * @param: @param orderAmount 订单金额 单位元
     * @param: @param desc 订单描述
     * @param: @param userId 下单用户id
     * @param: @param notifyUrl 回调地址
     * @param: @param timeExpire 订单过期时间，格式为YYYY-MM-DDTHH:mm:ss+TIMEZONE
     * @param: @param clientIp 客户端ip地址
     * @param: @return
     * @return: R
     */
    public String unifiedOrder(String orderNo, BigDecimal orderAmount, String desc, String userId, String notifyUrl, String timeExpire, String clientIp) {
        JSONObject ordert = new JSONObject();
        ordert.put("appid", appId);
        ordert.put("mchid", mchId);
        ordert.put("description", desc);
        ordert.put("out_trade_no", orderNo);
        ordert.put("time_expire", timeExpire);
        ordert.put("attach", userId);
        ordert.put("notify_url", notifyUrl);

        //订单金额
        JSONObject amount = new JSONObject();
        amount.put("total", orderAmount.multiply(BigDecimal.valueOf(100)).intValue());//单位为分
        amount.put("currency", "CNY");
        ordert.put("amount", amount);

        //场景信息
        JSONObject sceneInfo = new JSONObject();
        sceneInfo.put("payer_client_ip", clientIp);
        ordert.put("scene_info", sceneInfo);

        //结算信息
        JSONObject settleInfo = new JSONObject();
        settleInfo.put("profit_sharing", false);
        ordert.put("settle_info", settleInfo);
        return postV3(unifiedOrderUrl, ordert.toJSONString());
    }

    public String appletUnifiedOrder(com.alibaba.fastjson2.JSONObject ordert, String clientIp) {
        //场景信息
        JSONObject sceneInfo = new JSONObject();
        sceneInfo.put("payer_client_ip", clientIp);
        ordert.put("scene_info", sceneInfo);

        //结算信息
        JSONObject settleInfo = new JSONObject();
        settleInfo.put("profit_sharing", false);
        ordert.put("settle_info", settleInfo);
        return postV3(unifiedOrderUrl, ordert.toJSONString());
    }



    /**
     * 获取app客户端调用微信支付参数
     *
     * @throws
     * @Title: getWxAppPayParams
     * @param: @param prepayId
     * @param: @return
     * @return: WxAppPayVo
     */
    public WxAppPayVo getWxAppPayParams(String prepayId) {
        WxAppPayVo resp = new WxAppPayVo();
        resp.setAppid(this.appId);
        resp.setPartnerid(this.mchId);
        resp.setTimestamp(String.valueOf((System.currentTimeMillis() / 1000)));
        resp.setNoncestr(RandomStringUtils.randomAlphanumeric(26));
        resp.setPrepayid(prepayId);
        resp.setPackageStr("Sign=WXPay");

        String signStr = resp.getAppid() + "\n" + resp.getTimestamp() + "\n" + resp.getNoncestr() + "\n"
                + resp.getPrepayid() + "\n";

        String paySign = sign(signStr);
        resp.setSign(paySign);

        return resp;
    }

    public WxAppPayVo getAppletPayParams(String prepayId, Long orderId) {
        WxAppPayVo resp = new WxAppPayVo();
        resp.setAppid(this.appId);
        resp.setPartnerid(this.mchId);
        resp.setTimestamp(String.valueOf((System.currentTimeMillis() / 1000)));
        resp.setNoncestr(RandomStringUtils.randomAlphanumeric(26));
        resp.setPrepayid(prepayId);
        resp.setPackageStr("prepay_id=" +prepayId);
        resp.setSignType("RSA");
        String signStr = resp.getAppid() + "\n" + resp.getTimestamp() + "\n" + resp.getNoncestr() + "\n"
                + resp.getPackageStr() + "\n";

        String paySign = sign(signStr);
        resp.setSign(paySign);
        resp.setOrderId(orderId);
        return resp;
    }

    /**
     * apiV3 post请求
     *
     * @throws
     * @Title: postV3
     * @param: @param url
     * @param: @param reqdata
     * @param: @return
     * @return: R
     */
    public String postV3(String url, String reqdata) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Accept", "application/json");

        StringEntity reqEntity = new StringEntity(reqdata, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(reqEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            EntityUtils.consume(entity);

            log.info("\n postV3 url:{} reqdata:{} response:{}", url, reqdata, responseString);
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode || HttpStatus.SC_NO_CONTENT == statusCode) {
                JSONObject jsonObject = JSON.parseObject(responseString);
                return jsonObject.getString("prepay_id");
            } else {
                throw CommonException.FAILURE("调用微信支付失败");
            }
        } catch (Exception e) {
            log.error("\n postV3 url:{} reqdata:{} error:{}", url, reqdata, e.getMessage());
            throw CommonException.FAILURE("调用微信支付失败");
        } finally {
            httpPost.releaseConnection();
        }
    }

    /**
     * apiV3 get请求
     *
     * @throws
     * @Title: getV3
     * @param: @param url
     * @param: @return
     * @return: R
     */
    public CommonResultVo getV3(String url) throws Exception {
        return getV3(url, null);
    }

    /**
     * apiV3 get请求
     *
     * @throws
     * @Title: getV3
     * @Description:
     * @param: @param url
     * @param: @param nvps
     * @param: @return
     * @param: @throws Exception
     * @return: R
     */
    public CommonResultVo getV3(String url, List<NameValuePair> nvps) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (nvps != null) {
            uriBuilder.setParameters(nvps);
        }

        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Content-Type", "application/json");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);

        int statusCode = response.getStatusLine().getStatusCode();
        log.info("\n getV3 url:{} nvps:{} response:{}", url, nvps, responseString);
        if (HttpStatus.SC_OK == statusCode || HttpStatus.SC_NO_CONTENT == statusCode) {
            return CommonResultVo.SUCCESS(responseString);
        } else {
            Map<String, String> map = JSONArray.parseObject(responseString, Map.class);
            return CommonResultVo.FAILURE_MSG("调用微信支付失败");

        }
    }

    /**
     * 签名
     *
     * @throws
     * @Title: sign
     * @Description:
     * @param: @param message
     * @param: @return
     * @return: String
     */
    public String sign(String message) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(merchantPrivateKey);
            sign.update(message.getBytes(StandardCharsets.UTF_8));

            return Base64Utils.encodeToString(sign.sign());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名计算失败", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("无效的私钥", e);
        }

    }

    /**
     * 验证签名
     *
     * @throws
     * @Title: verify
     * @Description:
     * @param: @param serialNumber
     * @param: @param message
     * @param: @param signature
     * @param: @return
     * @return: boolean
     */
    public boolean verify(String serialNumber, String message, String signature) {
        return verifier.verify(serialNumber, message.getBytes(StandardCharsets.UTF_8), signature);
    }

    /**
     * 数据加密
     *
     * @throws
     * @Title: encryptOAEP
     * @Description:
     * @param: @param message
     * @param: @return
     * @return: String
     */
    public String encryptOAEP(String message) {
        try {
            return RsaCryptoUtil.encryptOAEP(message, verifier.getValidCertificate());
        } catch (Exception e) {
            log.error("数据加密 encryptOAEP error:{}", e.getMessage());
        }
        return "";
    }

    /**
     * 数据解密
     *
     * @throws
     * @Title: decryptToString
     * @Description:
     * @param: @param associatedData
     * @param: @param nonce
     * @param: @param ciphertext
     * @param: @return
     * @return: String
     */
    public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext) {
        AesUtil decryptor = new AesUtil(this.apiV3Key);
        try {
            return decryptor.decryptToString(associatedData, nonce, ciphertext);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            log.error("数据解密 decryptToString error:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("数据解密 decryptToString error:{}", e.getMessage());
        }
        return "";
    }

}

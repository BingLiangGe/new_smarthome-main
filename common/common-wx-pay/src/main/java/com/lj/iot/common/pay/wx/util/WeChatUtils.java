package com.lj.iot.common.pay.wx.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.lj.iot.common.pay.wx.WeChatPayV3;
import com.lj.iot.common.pay.wx.WxPayNotifyReq;
import com.lj.iot.common.pay.wx.WxPayResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * WeChat工具类
 */
@Slf4j
public class WeChatUtils {
    /**
     * 获取回调请求报文
     * @param request
     * @return
     * @throws IOException
     */
    public static String getWxPayNotifyRequestBody(HttpServletRequest request) throws IOException {
        ServletInputStream stream = null;
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        try {
            stream = request.getInputStream();
            // 获取响应
            reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new IOException("读取返回支付接口数据流出现异常！");
        } finally {
            reader.close();
        }
        return sb.toString();
    }

    /**
     * 支付状态
     * @param weChatPayV3
     * @param request
     * @param response
     * @return
     */
    public static WxPayResultVo paymentSuccess(WeChatPayV3 weChatPayV3, HttpServletRequest request, HttpServletResponse response){
       WxPayResultVo wxPayResultVo = new WxPayResultVo();
        String message = "";
        try {
            message = getWxPayNotifyRequestBody(request);
        } catch (Exception e) {
            log.error("WeChatUtils paymentSuccess error:", e);
        }
        WxPayNotifyReq wxPayNotifyReq = JSON.parseObject(message, WxPayNotifyReq.class);
        log.info("wxPayNotify:{}  message:{}", wxPayNotifyReq, message);
        String msg = wxPayNotifyReq.getSummary();
        String code = "FAIL";
        int httpCode = 500;
        try {
            //校验签名
            String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
            String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
            String wechatpaySignature = request.getHeader("Wechatpay-Signature");
            String wechatpaySerial = request.getHeader("Wechatpay-Serial");
            String str = wechatpayTimestamp + "\n" + wechatpayNonce + "\n" + message + "\n";
            log.info("wxPayNotify str:{}", str);
            boolean isFlag = weChatPayV3.verify(wechatpaySerial, str, wechatpaySignature);
            log.info("wxPayNotify isFlag:{}", isFlag);
            //校验不通过直接返回
            if (!isFlag) {
                writeMessage(response, httpCode, code, msg);
                log.error("weChatPayV3 verify fail, 支付失败");
                return wxPayResultVo;
            }

            WxPayNotifyReq.PayResource payResource = wxPayNotifyReq.getResource();

            if(payResource == null){
                writeMessage(response, httpCode, code, msg);
                log.error("payResource is null, 支付失败");
                return wxPayResultVo;
            }

            if(!StringUtils.equalsIgnoreCase(wxPayNotifyReq.getEvent_type(), "TRANSACTION.SUCCESS")
                    || !StringUtils.equalsIgnoreCase(wxPayNotifyReq.getResource_type(), "encrypt-resource")){
                writeMessage(response, httpCode, code, msg);
                log.error("event_type!=TRANSACTION.SUCCESS or resource_type!=encrypt-resource, 支付失败");
                return wxPayResultVo;
            }

            String decrypt = weChatPayV3.decryptToString(payResource.getAssociated_data().getBytes(StandardCharsets.UTF_8)
                    , payResource.getNonce().getBytes(StandardCharsets.UTF_8), payResource.getCiphertext());
            if(StringUtils.isBlank(decrypt)){
                log.error("decrypt:{}", decrypt);
                log.error("decrypt is null, 支付失败");
                writeMessage(response, httpCode, code, msg);
                return wxPayResultVo;
            }
            log.info("decrypt:{}", decrypt);
            //支付成功
            httpCode = 200;
            code = "SUCCESS";
            msg = "支付成功";
            log.info("支付成功");

            JSONObject resultData = JSON.parseObject(decrypt);
            //处理业务逻辑
            if (resultData != null && resultData.size() > 0) {
                String orderNo = resultData.getString("out_trade_no");
                String transactionId = resultData.getString("transaction_id");
                log.debug("wxpayOrder orderNo:{} transactionId:{}", orderNo, transactionId);
                wxPayResultVo.setOrderNo(orderNo);
                wxPayResultVo.setTransactionId(transactionId);
            }
            writeMessage(response, httpCode, code, msg);
        } catch (Exception e) {
            log.error("微信支付回调报错:", e);
            writeMessage(response, httpCode, code, msg);
        }
        return wxPayResultVo;
    }

    /**
     * 支付状态
     * @param weChatPayV3
     * @param request
     * @param response
     * @return
     */
    public static WxPayResultVo refundSuccess(WeChatPayV3 weChatPayV3, HttpServletRequest request, HttpServletResponse response){
        WxPayResultVo wxPayResultVo = new WxPayResultVo();
        String message = "";
        try {
            message = getWxPayNotifyRequestBody(request);
        } catch (Exception e) {
            log.error("WeChatUtils refundSuccess error:", e);
        }
        log.info(" message:{}",  message);
        WxPayNotifyReq wxPayNotifyReq = JSON.parseObject(message, WxPayNotifyReq.class);
        log.info("wxPayNotify:{}  message:{}", wxPayNotifyReq, message);
        String msg = wxPayNotifyReq.getSummary();
        String code = "FAIL";
        int httpCode = 500;
        try {
            //校验签名
            String wechatpayTimestamp = request.getHeader("Wechatpay-Timestamp");
            String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
            String wechatpaySignature = request.getHeader("Wechatpay-Signature");
            String wechatpaySerial = request.getHeader("Wechatpay-Serial");
            String str = wechatpayTimestamp + "\n" + wechatpayNonce + "\n" + message + "\n";
            log.info("wxRefundNotify str:{}", str);
            boolean isFlag = weChatPayV3.verify(wechatpaySerial, str, wechatpaySignature);
            log.info("wxRefundNotify isFlag:{}", isFlag);
            //校验不通过直接返回
            if (!isFlag) {
                writeMessage(response, httpCode, code, msg);
                log.error("weChatPayV3 verify fail, 退款失败");
                return wxPayResultVo;
            }

            WxPayNotifyReq.PayResource payResource = wxPayNotifyReq.getResource();

            if(payResource == null){
                writeMessage(response, httpCode, code, msg);
                log.error("payResource is null, 退款失败");
                return wxPayResultVo;
            }

            if(!StringUtils.equalsIgnoreCase(wxPayNotifyReq.getEvent_type(), "REFUND.SUCCESS")){
                writeMessage(response, httpCode, code, msg);
                log.error("event_type!=TRANSACTION.SUCCESS or resource_type!=encrypt-resource, 退款失败");
                return wxPayResultVo;
            }

            String decrypt = weChatPayV3.decryptToString(payResource.getAssociated_data().getBytes(StandardCharsets.UTF_8)
                    , payResource.getNonce().getBytes(StandardCharsets.UTF_8), payResource.getCiphertext());
            if(StringUtils.isBlank(decrypt)){
                log.error("decrypt:{}", decrypt);
                log.error("decrypt is null, 退款失败");
                writeMessage(response, httpCode, code, msg);
                return wxPayResultVo;
            }
            log.info("decrypt:{}", decrypt);
            //支付成功
            httpCode = 200;
            code = "SUCCESS";
            msg = "支付成功";
            log.info("支付成功");

            JSONObject resultData = JSON.parseObject(decrypt);
            //处理业务逻辑
            if (resultData != null && resultData.size() > 0) {
                String outRefundNo = resultData.getString("out_refund_no");
                String refundId = resultData.getString("refund_id");
                String transactionId = resultData.getString("transaction_id");
                String refundStatus = resultData.getString("refund_status");
                String success_time = resultData.getString("success_time");
                wxPayResultVo.setOrderNo(outRefundNo);
                wxPayResultVo.setTransactionId(transactionId);
                wxPayResultVo.setRefundId(refundId);
                wxPayResultVo.setRefundStatus(refundStatus);
                wxPayResultVo.setTransactionId(transactionId);
                LocalDateTime localDateTime = convertTimeZoneStringToLocalDateTime(success_time);
                wxPayResultVo.setSuccessTime(localDateTime);
            }
            writeMessage(response, httpCode, code, msg);
        } catch (Exception e) {
            log.error("微信退款回调报错:", e);
            writeMessage(response, httpCode, code, msg);
        }
        return wxPayResultVo;
    }

    public static LocalDateTime convertTimeZoneStringToLocalDateTime(String timeZoneDateTimeStr) {
        DateTime parse = DateUtil.parse(timeZoneDateTimeStr);
        Instant instant = parse.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static void writeMessage(HttpServletResponse response, int httpCode, String code, String message) {
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            writer = response.getWriter();
            response.setStatus(httpCode);

            Map<String, String> map = Maps.newHashMap();
            map.put("code", code);
            map.put("message", message);

            ObjectMapper mapper = new ObjectMapper();
            writer.println(mapper.writeValueAsString(map));
        } catch (IOException e) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

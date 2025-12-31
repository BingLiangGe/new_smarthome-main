package com.lj.iot.common.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.lj.iot.common.sms.config.SendSmsUtilQy;
import com.lj.iot.common.sms.properties.SmsProperties;
import com.lj.iot.common.sms.service.ISmsService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DefaultSmsServiceImpl implements ISmsService {

    private final Client client;
    private final SmsProperties properties;

    public DefaultSmsServiceImpl(Client client, SmsProperties properties) {
        this.client = client;
        this.properties = properties;
    }


    @Override
    public String sendVerificationCode(String mobile) {
        log.info("DefaultSmsServiceImpl.sendVerificationCode不发短信环境，直接默认123456");
        return "123456";
    }

    @Override
    public String sendVerificationCodeQy(String mobile) {
        return null;
    }

    @Override
    public boolean sendForHelp(String mobile, String deviceName) {
        log.info("DefaultSmsServiceImpl.sendForHelp，不发短信");
        return true;
    }

    @Override
    public boolean send(String mobile, String templateCode, Map<String, Object> params) {
        final SendSmsRequest request = new SendSmsRequest();
        request.setSignName(properties.getSignName());
        request.setPhoneNumbers(mobile);
        request.setTemplateCode(templateCode);
        request.setTemplateParam(JSON.toJSONString(params));

        try {
            SendSmsResponse response = client.sendSms(request);
            return "OK".equalsIgnoreCase(response.getBody().getCode());
        } catch (Exception e) {
            log.error("SMS Send Failed.", e);
        }
        return false;
    }

    @Override
    public boolean sendQy(String mobile, String code) {
        return SendSmsUtilQy.sendSmsUtil(mobile,code);
    }
}

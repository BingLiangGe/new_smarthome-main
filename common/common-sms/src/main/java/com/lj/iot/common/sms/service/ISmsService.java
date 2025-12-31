package com.lj.iot.common.sms.service;

import java.util.Map;

public interface ISmsService {


    boolean send(String mobile, String templateCode, Map<String, Object> params);

    boolean sendQy(String mobile, String code);


    String sendVerificationCode(String mobile);

    String sendVerificationCodeQy(String mobile);

    boolean sendForHelp(String mobile, String deviceName);
}

package com.lj.iot.common.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sms.ali")
public class SmsProperties {

    private String accessKeyId;
    private String accessKeyCCCFDF;
    private String regionId;
    private String domain = "dysmsapi.aliyuncs.com";

    private String signName = "奥美家智能电器";
    private String sendCodeKey = "SMS_232160598";
    private String sendHelpKey = "SMS_232175694";
}

package com.lj.iot.commom.vms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "vms.ali")
public class VmsProperties {
    private String accessKeyId;
    private String accessCCCFDF;
    private int nThread = 1;
    private String regionId = "cn-hangzhou";
    private String domain = "dyvmsapi.aliyuncs.com";
    private String ttsCode = "TTS_241057471";
    private String calledShowNumber = "02122798759";
}

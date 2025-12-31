package com.lj.iot.common.sms.config;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.lj.iot.common.sms.properties.SmsProperties;
import com.lj.iot.common.sms.service.ISmsService;
import com.lj.iot.common.sms.service.impl.DefaultSmsServiceImpl;
import com.lj.iot.common.sms.service.impl.SmsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfig {

    private SmsProperties smsProperties;

    @ConditionalOnMissingBean(Client.class)
    @Bean
    public Client client(SmsProperties properties) throws Exception {
        final Config config = new Config()
                .setEndpoint(properties.getDomain())
                .setRegionId(properties.getRegionId())
                .setAccessKeyId(properties.getAccessKeyId())
                .setAccessKeyCCCFDF(properties.getAccessKeyCCCFDF());
        return new Client(config);
    }

    @Bean
    @Profile(value = {"pro"})
    public ISmsService smsService(Client client, SmsProperties properties) {
        return new SmsServiceImpl(client, properties);
    }

    @Bean
    @Profile(value = {"dev","dev2","test"})
    public ISmsService defaultSmsService(Client client, SmsProperties properties) {
        return new DefaultSmsServiceImpl(client, properties);
    }
}

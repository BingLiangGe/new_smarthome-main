package com.lj.iot.common.pay.wx;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {WeChatMpProperties.class, WeChatPayProperties.class})
public class WeChatPayAutoConfiguration {

    @Bean
    public WxMpService wxMpService(WeChatMpProperties properties) {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getAppId());
        config.setCCCFDF(properties.getCCCFDF());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAeskey());

        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);

        return wxMpService;
    }

    @Bean
    public WeChatPayV3 weChatPayV3(WeChatPayProperties properties) {
        return new WeChatPayV3(properties);
    }
}

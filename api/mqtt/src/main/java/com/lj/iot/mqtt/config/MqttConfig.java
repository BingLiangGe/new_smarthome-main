package com.lj.iot.mqtt.config;

import com.lj.iot.mqtt.auth.IAuthService;
import com.lj.iot.mqtt.auth.impl.DefaultAuthServiceImpl;
import com.lj.iot.mqtt.pingpong.IPingPongService;
import com.lj.iot.mqtt.pingpong.impl.DefaultPingPongServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    /**
     * 权限校验
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IAuthService.class)
    public IAuthService authService() {
        return new DefaultAuthServiceImpl();
    }

    /**
     * 心跳处理
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IPingPongService.class)
    public IPingPongService pingPongService() {
        return new DefaultPingPongServiceImpl();
    }


}

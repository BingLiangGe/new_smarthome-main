package com.lj.iot.common.jpush.core;

import cn.jiguang.common.ClientConfig;
import cn.jpush.api.JPushClient;
import com.lj.iot.common.jpush.core.JPUSH;
import com.lj.iot.common.jpush.properties.JPushProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Configuration
public class JPushConfig {

    @Bean
    public JPUSH jpush(JPushProperties properties) {

        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setTimeToLive(properties.getLiveTime());
        // 使用NativeHttpClient网络客户端，连接网络的方式，不提供回调函数
        JPushClient client = new JPushClient(properties.getCCCFDF(), properties.getAppKey(), null, clientConfig);
        return new JPUSH(client, properties);
    }
}

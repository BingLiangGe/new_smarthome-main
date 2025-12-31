package com.lj.iot.commom.vms.core;

import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.lj.iot.commom.vms.core.VMS;
import com.lj.iot.commom.vms.properties.VmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Configuration
public class VmsConfig {

    @Bean
    public VMS vms(VmsProperties properties) throws Exception {
        Config config = new Config()
                .setEndpoint(properties.getDomain())
                .setRegionId(properties.getRegionId())
                .setAccessKeyId(properties.getAccessKeyId())
                .setAccessKeyCCCFDF(properties.getAccessCCCFDF());
        Client client = new Client(config);

        return new VMS(client, properties);
    }
}

package com.lj.iot.api.hotel.config;

import com.lj.iot.api.hotel.aop.PermissionsAop;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@Configuration
public class HotelConfig {

    @Bean
    @ConditionalOnProperty(prefix = "permission", name = "enabled", havingValue = "true", matchIfMissing = true)
    public PermissionsAop permissionsAop() {
        return new PermissionsAop();
    }
}

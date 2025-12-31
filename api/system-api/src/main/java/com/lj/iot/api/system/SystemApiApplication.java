package com.lj.iot.api.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
/*@RefreshScope
@EnableDiscoveryClient*/
//@EnableEurekaClient
@EnableCaching
@EnableFeignClients(basePackages = "com.lj.iot.*")
@MapperScan(basePackages = {"com.lj.iot.biz.db.smart.mapper","com.lj.iot.common.system.mapper"})
@SpringBootApplication(scanBasePackages = {"com.lj.*"})
public class SystemApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApiApplication.class, args);
    }

}

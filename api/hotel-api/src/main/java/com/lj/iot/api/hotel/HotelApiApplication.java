package com.lj.iot.api.hotel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableTransactionManagement
@EnableAspectJAutoProxy
@RefreshScope
@EnableDiscoveryClient
@EnableEurekaClient
/*@RefreshScope
@EnableDiscoveryClient*/
@EnableCaching
@EnableFeignClients(basePackages = "com.lj.iot.*")
@MapperScan(basePackages = {"com.lj.iot.biz.db.smart.mapper"})
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
public class HotelApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApiApplication.class, args);
    }

}

package com.lj.iot.api.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@RefreshScope
@EnableDiscoveryClient
/*@RefreshScope
@EnableDiscoveryClient*/
@EnableEurekaClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.lj.iot.*")
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}

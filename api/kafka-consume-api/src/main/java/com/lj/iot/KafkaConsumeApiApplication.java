package com.lj.iot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@ComponentScan(excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX,pattern = "com.lj.iot.biz.service.mqtt.handler.*") })
@MapperScan(basePackages = {"com.lj.iot.biz.db.smart.mapper"})
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
@EnableFeignClients(basePackages = "com.lj.iot.*")
@RefreshScope
@EnableDiscoveryClient
public class KafkaConsumeApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumeApiApplication.class,args);
    }
}

package com.lj.iot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
@RefreshScope
@EnableDiscoveryClient
public class MqToKafkaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.lj.iot.MqToKafkaApiApplication.class, args);
    }
}

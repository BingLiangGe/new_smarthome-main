package com.lj.iot.watchnetty;

import com.lj.iot.watchnetty.server.BootNettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAsync
@EnableAspectJAutoProxy
@EnableEurekaClient
@EnableCaching
@EnableFeignClients(basePackages = "com.lj.iot.*")
@MapperScan(basePackages = {"com.lj.iot.biz.db.smart.mapper"})
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
public class WatchNettyApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WatchNettyApplication.class, args);
    }


    @Async
    @Override
    public void run(String... args) throws Exception {
        /**
         * 使用异步注解方式启动netty服务端服务
         */
        new BootNettyServer().bind(9998);
    }
}

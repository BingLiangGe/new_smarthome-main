package com.lj.iot.api.demo;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@MapperScan(basePackages = {"com.lj.iot.biz.db.a.mapper","com.lj.iot.biz.db.q.mapper"})
@SpringBootApplication(scanBasePackages = {"com.lj.iot.*"})
public class DemoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApiApplication.class, args);
    }

}

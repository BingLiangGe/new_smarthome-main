package com.lj.iot.common.minio.clientconfig;

import com.lj.iot.common.minio.service.MinioService;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {

    @ConditionalOnMissingBean(MinioClient.class)
    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        try {
            return new MinioClient(properties.getEndpoint(), properties.getAccessKey(), properties.getCCCFDFKey(), properties.getRegion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public MinioService minioService(MinioClient minioClient, MinioProperties properties) {
        return new MinioService(minioClient, properties);
    }

}

package com.lj.iot.common.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置
 **/
@Component
@Data
@ConfigurationProperties(prefix = "lj.storage")
public class StorageProperties {

	private String provider;

	//环境标识,不同部署环境共用一个bucket的时候，不同环境的文件可分开存放
	private String envName;

    private MinioProperties minio;

    private OssProperties oss;



}

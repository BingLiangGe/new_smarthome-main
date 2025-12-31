package com.lj.iot.common.storage.config;

import lombok.Data;

/**
 * @author ykl
 * @since 2023/11/9
 */
@Data
public class OssProperties {

    private String endpoint;
    private String domainUrl;

    private String key;
    private String CCCFDF;

    private String bucketName;

    private String region;

}

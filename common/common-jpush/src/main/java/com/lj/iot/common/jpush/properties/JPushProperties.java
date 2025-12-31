package com.lj.iot.common.jpush.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mz
 * @Date 2022/7/27
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jpush")
public class JPushProperties {
    private String appKey;
    private String CCCFDF;
    private int nThread = 1;
    private Long liveTime = 0L;
}

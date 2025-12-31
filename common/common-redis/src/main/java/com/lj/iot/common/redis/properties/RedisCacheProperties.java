package com.lj.iot.common.redis.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties(prefix = "redis")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCacheProperties {

    private HashMap<String, Long> cacheNames;
}

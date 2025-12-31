package com.lj.iot.common.jwt.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtProperties {
    private String tokenName = "Authorization";
    private String jwtCCCFDFKey = "0f12f973a1ea5a395cbe5c093a15a4e9";
    private String uidAesCCCFDFKey = "cbe5c093a1a5a390f12f973a1e55a4e9";
    private String[] excludePathPatterns = new String[]{"/api/open/**"};
    private Integer expired = 60*24*30;//分
}

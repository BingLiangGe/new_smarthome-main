package com.lj.iot.common.sso.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sso")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SSOProperties {


    private String tokenName = "Authorization";
    private String app = "default";
    private Integer expired = 7200;//秒
    /**
     * 排除登录校验拦截地址
     */
    private String[] excludeLoginPathPatterns;

    /**
     * 排除多点登录校验
     */
    private String[] excludeMultipointLoginPathPatterns;

    /**
     * 排除google认证
     */
    private String[] excludeGoogleAuthPathPatterns;
}

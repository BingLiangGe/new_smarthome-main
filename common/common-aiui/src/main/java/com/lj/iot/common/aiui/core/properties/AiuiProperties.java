package com.lj.iot.common.aiui.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "aiui")
public class AiuiProperties {

    /**
     * 上传地址
     */
    private String uploadUrl;
    /**
     * 校验地址
     */
    private String checkUrl;

    /**
     * ws地址
     */
    private String wsUrl;

    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 账户Key
     */
    private String accountKey;
    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用KEY
     */
    private String appKey;
    /**
     * 用户级
     * 资源名 https://aiui.xfyun.cn/studio/entity/{动态实体ID}
     * 下列表中的资源名称 namespace.resourceName
     */
    //private String userResourceName;

    /**
     * 应用级
     * 资源名 https://aiui.xfyun.cn/studio/entity/{动态实体ID}
     * 下列表中的资源名称 namespace.resourceName
     */
    //private String appResourceName;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 支持技能
     */
    private Set<String> supportIntents = new HashSet<>();
}

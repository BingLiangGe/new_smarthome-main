package com.lj.iot.common.aiui.core.config;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.IIdentifyingTextPostProcessor;
import com.lj.iot.common.aiui.core.service.ISkillPostProcessor;
import com.lj.iot.common.aiui.core.service.ISkillWsAckProcessor;
import com.lj.iot.common.aiui.core.vo.AIUIResultVo;
import com.lj.iot.common.aiui.core.vo.IntentResult;
import com.lj.iot.common.aiui.core.ws.AiuiWsClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class AIUIConfig {

    @Bean
    public AiuiWsClient aiuiWsClient(AiuiProperties properties) {
        return new AiuiWsClient(properties);
    }

    @Bean
    @Order(99999)
    @ConditionalOnMissingBean(ISkillPostProcessor.class)
    public ISkillPostProcessor skillPostProcessor() {
        return (IntentDto intentDto) -> {
            log.info("AIUIConfig.skillPostProcessor" + intentDto.toString());
        };
    }

    @Bean
    @Order(99999)
    @ConditionalOnMissingBean(IIdentifyingTextPostProcessor.class)
    public IIdentifyingTextPostProcessor identifyingTextPostProcessor() {
        return (IntentDto intentDto) -> {
            log.info("AIUIConfig.identifyingTextPostProcessor" + intentDto.toString());
        };
    }

    @Bean
    @Order(99999)
    @ConditionalOnMissingBean(ISkillWsAckProcessor.class)
    public ISkillWsAckProcessor skillWsAckProcessor() {
        return (JSONObject jsonObject) -> {
            log.info("AIUIConfig.skillWsAckProcessor" + jsonObject.toString());
        };
    }


}

package com.lj.iot.config;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONValidator;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.ITopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Configuration
public class ConsumerProcessor {
    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    @Lazy
    private MqttPushService mqttPushService;

    @Bean
    public Consumer<Message<String>> serviceLoginTokenReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceLoginTokenReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    private boolean validateDeviceExist(HandleMessage handleMessage) {
        String topicDeviceId = handleMessage.getTopicDeviceId();
        if (StringUtils.isBlank(topicDeviceId)) {
            log.info("validateDeviceExist====={}", "设备不存在");
            mqttPushService.noExistDevice(handleMessage.getTopicProductId(), topicDeviceId);
            return false;
        }

        UserDevice userDevice = userDeviceService.getById(topicDeviceId);
        if (userDevice == null) {
            log.info("MqttMessageHandler.onMessage====={}", "设备不存在");
            mqttPushService.noExistDevice(handleMessage.getTopicProductId(), topicDeviceId);
            return false;
        }
        return true;
    }

    @Bean
    public Consumer<Message<String>> serviceTopologyOtaReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceTopologyOtaReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceDeviceBindReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSysConnected() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSysConnected 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSysDisconnected() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSysDisconnected 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSos() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSos 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceSignalStudyRepay() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceSignalStudyRepay 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }


    @Bean
    public Consumer<Message<String>> servicePropertiesSetReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("servicePropertiesSetReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSubDeviceLine() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSubDeviceLine 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventTopologyDelete() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventTopologyDelete 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceTopologyAddReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceTopologyAddReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceTopologyDeleteReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceTopologyDeleteReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> pubDeviceHash() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("pubDeviceHash 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceMusicChangeReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceMusicChangeReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> serviceMeshUnbindReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("serviceMeshUnbindReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventTopologyList() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventTopologyList 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSubPlayerReply() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSubPlayerReply 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventDeviceHash() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventDeviceHash 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventPropertiesPost() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventPropertiesPost 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSubDeviceBindSuccess() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            log.info("eventSubDeviceBindSuccess 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> pubSubSubDevice() {
        return (data) -> {
            String payload = data.getPayload();
            log.info("pubSubSubDevice 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSubDeviceNetwork() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            Object headerFor = headers.get("for");
            log.info("eventSubDeviceNetwork 接收一条记录：{}", payload);
            log.info("eventSubDeviceNetwork getHeaders headerFor：{}", headerFor);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> eventSubDeviceOffline() {
        return (data) -> {
            String payload = data.getPayload();
            MessageHeaders headers = data.getHeaders();
            log.info("eventSubDeviceOffline 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    @Bean
    public Consumer<Message<String>> lock() {
        return (data) -> {
            String payload = data.getPayload();
            log.info("lock 接收一条记录：{}", payload);
            this.handlerEntrance(data.getPayload());
        };
    }

    private void handlerEntrance(String payload) {
        JSONValidator jsonValidator = JSONValidator.from(payload);
        if(jsonValidator.validate()){
            HandleMessage handleMessage = null;
            try {
                handleMessage = JSONObject.parseObject(payload, HandleMessage.class);
            }catch (Exception e){
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(payload);
                handleMessage = new HandleMessage();
                handleMessage.setTopic(jsonObject.getString("topic"));
                handleMessage.setTopicDeviceId(jsonObject.getString("topicDeviceId"));
                handleMessage.setTopicProductId(jsonObject.getString("topicProductId"));
                String body = jsonObject.getString("body");
                if(JSON.isValid(body)){
                    handleMessage.setBody(jsonObject.getJSONObject("body"));
                }else {
                    com.alibaba.fastjson.JSONObject content = new com.alibaba.fastjson.JSONObject();
                    content.put("body", body);
                    handleMessage.setBody(content);
                }
            }

            boolean flag = validateDeviceExist(handleMessage);
            if (!flag) {
                return;
            }
            SubTopicEnum subTopicEnum = SubTopicEnum.parse(handleMessage.getTopic());
            Map<String, ITopicHandler> handlers = SpringUtil.getBeansOfType(ITopicHandler.class);
            for (Map.Entry<String, ITopicHandler> entry : handlers.entrySet()) {
                final ITopicHandler handler = entry.getValue();
                if (handler.isSupport(subTopicEnum)) {
                    handler.entrance(handleMessage);
                }
            }
        }else{
            log.info("=======================接受到了空消息=======================");
        }
    }
}

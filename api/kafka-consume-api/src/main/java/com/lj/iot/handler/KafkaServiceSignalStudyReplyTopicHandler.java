package com.lj.iot.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mz
 * @Date 2022/8/2
 * @since 1.0.0
 */
@Slf4j
@Component
public class KafkaServiceSignalStudyReplyTopicHandler extends AbstractTopicHandler {

    public KafkaServiceSignalStudyReplyTopicHandler() {
        setSupportTopic(SubTopicEnum.SERVICE_SIGNAL_STUDY_REPAY);
    }

    private Integer ZERO = Integer.parseInt("0");
    @Resource
    private IUserDeviceService userDeviceService;

    @Resource
    private MqttPushService mqttPushService;

    @Resource
    @Lazy
    private BizWsPublishService bizWsPublishService;

    @Resource
    private IRfModelService rfModelService;

    @Resource
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    /**
     * 射频学码数据上报
     * {
     * "id": "123",  //消息ID
     * "code": 0,    //0:成功  -1:失败
     * "data":{
     * "signalType":"IR", //信号类型  IR\|RF
     * "keyId": 12,  //按键ID
     * "productId": "56789", //产品ID
     * "deviceId": "123456", //设备ID
     * "value": [89,23,23,...] //学到的码值
     * },
     * "msg": "success",   //消息描述
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {
        log.info("ServiceSignalStudyReplyTopicHandler.handle{}", JSON.toJSONString(message));
        UserDevice userDevice = userDeviceService.getById(message.getTopicDeviceId());

        JSONObject body = message.getBody();

        final JSONObject codeData = body.getJSONObject("data");
        JSONArray codeValue = codeData.getJSONArray("value");

        List<UserDeviceRfKey> userDeviceRfKeys = userDeviceRfKeyService.list(new QueryWrapper<>(UserDeviceRfKey.builder()
                .deviceId(codeData.getString("deviceId"))
                .build()));
        Long modelId = codeData.getLong("modelId");
        RfModel rfModel = rfModelService.getById(modelId);
        if (ZERO.compareTo(body.getInteger("code")) != 0 || codeValue.size() <= 19 || userDeviceRfKeys.size() <= 0) {
            log.error("SignalStudyReplyTopicHandler.handle:学码失败");
            bizWsPublishService.publishAllMemberByHomeIdFailure(RedisTopicConstant.TOPIC_CHANNEL_STUDY,
                    userDevice.getHomeId(),
                    body.get("data")
            );
            return;
        }
        for (UserDeviceRfKey udr : userDeviceRfKeys) {
            //风扇灯特殊处理
            if ("3000001000".equals(userDevice.getProductId())) {
                udr.setCodeData(StringUtils.join(codeValue, ","));
                continue;
            }
            if ("5".equals(codeValue.get(11))) {
                //杜亚设备
                codeValue.set(16, Integer.toHexString(udr.getKeyIdx()));
            } else if ("7".equals(codeValue.get(11)) && "F202".equals(rfModel.getCodeType())) {
                codeValue.set(17, Integer.toHexString(udr.getKeyIdx()));
                String code = "";
                for (int i = 13; i < 18; i++) {
                    if (i == 13) {
                        code = codeValue.getString(i);
                    } else {
                        code = xorString(code, codeValue.getString(i));
                    }
                }
                codeValue.set(18, code);
            } else if ("7".equals(codeValue.get(11))) {
                codeValue.set(17, Integer.toHexString(udr.getKeyIdx()));
                String code = "";
                for (int i = 13; i < 18; i++) {
                    if (i == 13) {
                        code = codeValue.getString(i);
                    } else {
                        code = xorString(code, codeValue.getString(i));
                    }
                }
                codeValue.set(18, code);
            } else if ("8".equals(codeValue.get(11))) {
                codeValue.set(18, Integer.toHexString(udr.getKeyIdx()));
                Long a = Long.parseLong(codeValue.getString(18), 16)
                        + Long.parseLong(codeValue.getString(13), 16)
                        + Long.parseLong(codeValue.getString(14), 16)
                        + Long.parseLong(codeValue.getString(15), 16)
                        + Long.parseLong(codeValue.getString(16), 16)
                        + Long.parseLong(codeValue.getString(17), 16);
                String s = Long.toHexString(a & 0xFF);
                codeValue.set(19, s);
            }
            udr.setCodeData(StringUtils.join(codeValue, ","));
        }
        userDeviceRfKeyService.saveOrUpdateBatch(userDeviceRfKeys);
        //推送数据到所有主控设备
        mqttPushService.pushOfficeData(userDevice.getHomeId(), OfflineTypeEnum.OFFLINE_EDIT, PubTopicEnum.PUB_RF_KEY_CHANGE, userDeviceRfKeys);

        bizWsPublishService.publishAllMemberByHomeId(
                RedisTopicConstant.TOPIC_CHANNEL_STUDY,
                userDevice.getHomeId(),
                userDeviceRfKeys
        );
    }

    private static String xorString(String strHex_X, String strHex_Y) {
        //将x、y转成二进制形式
        String anotherBinary = Integer.toBinaryString(Integer.valueOf(strHex_X, 16));
        String thisBinary = Integer.toBinaryString(Integer.valueOf(strHex_Y, 16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary = "0" + anotherBinary;
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary = "0" + thisBinary;
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        return Integer.toHexString(Integer.parseInt(result, 2));
    }

}

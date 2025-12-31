package com.lj.iot.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ToPinYin;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询子设备数据
 *
 * @author tyj
 * @Date 2023-7-4 14:54:08
 */
@Slf4j
@Component
public class KafkaEventSubDeviceListTopicHandler extends AbstractTopicHandler {

    public KafkaEventSubDeviceListTopicHandler() {
        setSupportTopic(SubTopicEnum.PUB_SUB_SUB_DEVICE);
    }

    @Resource
    private IUserDeviceService userDeviceService;


    @Override
    public void handle(HandleMessage message) {

        UserDevice masterUserDevice = userDeviceService.getById(message.getTopicDeviceId());

        ValidUtils.isNullThrow(masterUserDevice, "设备不存在");

        log.info("进入查询子设备,deviceId=#{}",
                masterUserDevice.getDeviceId());

        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("master_device_id", masterUserDevice.getDeviceId());
        wrapper.eq("is_show", true);
        List<UserDevice> list = userDeviceService.list(wrapper);
        List<Map> resultList = new ArrayList<>();
        for (UserDevice userDevice :
                list) {

            // 房间锁_大门锁不下发subdevice
            if (userDevice.getProductId().equals("9337719") || userDevice.getProductId().equals("9337720")) {
                continue;
            }

            Map resultMap = new HashMap<>();
            ThingModel thingModel = userDevice.getThingModel();
            Map<String, ThingModelProperty> map = thingModel.thingModel2Map();
            List<Map<String, Object>> ThingModelMapList = new ArrayList<>();
            for (String key :
                    map.keySet()) {
                //对每个map进行名称替换
                ThingModelProperty thingModelProperty = map.get(key);
                String pinYin = ToPinYin.getPinYin(thingModelProperty.getName());
                ThingModelMapList.add(new HashMap<>() {{
                    put("name", pinYin);
                    put("identifier", key);
                    put("value", thingModelProperty.getValue());
                }});
            }
            String customName = ToPinYin.getPinYin(userDevice.getCustomName());
            resultMap.put("deviceId", userDevice.getDeviceId());
            resultMap.put("status", userDevice.getStatus());
            resultMap.put("productId", userDevice.getProductId());
            if (customName.equals("kong1 diao4")) {
                customName = "kong1 tiao2";
            }else if (customName.equals("cha2 ba5 ji1")){
                customName = "cha2 ba1 ji1";
            } else if (customName.equals("jiu3 ju3 deng1")) {
                customName = "jiu3 gui4 deng1";
            }
            resultMap.put("customName", customName);
            resultList.add(resultMap);
        }

        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_SUB_DEVICE_REPLY, masterUserDevice.getProductId(), masterUserDevice.getDeviceId());

        MQTT.publish(topic, JSON.toJSONString(resultList));
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(resultList));
    }
}

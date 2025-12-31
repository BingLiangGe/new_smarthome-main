package com.lj.iot.biz.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.AirControlWord;
import com.lj.iot.biz.db.smart.entity.SpeechRecord;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IAirControlWordService;
import com.lj.iot.biz.db.smart.service.ISpeechRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.biz.service.enums.SubTopicEnum;
import com.lj.iot.biz.service.mqtt.AbstractTopicHandler;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备红外离线接受
 *
 * @author tyj
 * @Date 2023-6-8 11:41:48
 */
@Slf4j
@Component
public class EventSubDeviceOfflineCodeTopicHandler extends AbstractTopicHandler {

    public EventSubDeviceOfflineCodeTopicHandler() {
        setSupportTopic(SubTopicEnum.EVENT_SUB_DEVICE_OFFLINE);
    }

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    IAirControlWordService airControlWordService;

    @Resource
    private BizIrDeviceService bizIrDeviceService;

    @Resource
    private ISpeechRecordService speechRecordService;


    /**
     * 设备红外离线接受
     * {
     * "masterId": "13e1da5adb02", //消息ID
     * "keyWord": kong1%20tiao2%20shi2%20ba1%20du4 //时间
     * }
     *
     * @param message
     */
    @Override
    public void handle(HandleMessage message) {

        JSONObject jsonObject = message.getBody();

        String keyWord = jsonObject.getString("keyWord");
        String masterId = jsonObject.getString("masterId");

        log.info("getIrCode----->masterId:{},keyWork:{}", masterId, keyWord);

        AirControlWord one = airControlWordService.getOne(new QueryWrapper<>(AirControlWord.builder().pinyin(keyWord).build()));
        ValidUtils.isNullThrow(one, "控制命令不存在");
        //根据主控id来查用户
        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("master_device_id", masterId);
        wrapper.eq("is_show", true);
        wrapper.eq("device_name", one.getDeviceType());
        List<UserDevice> list = userDeviceService.list(wrapper); //主控下面的设备列表
        if (list.size() > 0) {

            for (UserDevice userDevice :
                    list) {
                String keyCode = one.getKeyCode();
                ThingModel thingModel = userDevice.getThingModel();
                List<ThingModelProperty> properties = thingModel.getProperties();
                List<ThingModelProperty> changeList = null;
                List<ThingModelProperty> otherList = null;
                if (one.getDeviceType().equals("空调")) {
                    if (keyCode.equals("close") || keyCode.equals("open")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("powerstate")).collect(Collectors.toList());
                        otherList = properties.stream().filter(it -> !it.getIdentifier().contains("powerstate")).collect(Collectors.toList());
                    } else {
                        String key = keyCode;
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains(key)).collect(Collectors.toList());
                        otherList = properties.stream().filter(it -> !it.getIdentifier().contains(key)).collect(Collectors.toList());
                        for (ThingModelProperty thingModelProperty : otherList) {
                            if (thingModelProperty.getIdentifier().equals("powerstate")) {
                                thingModelProperty.setValue("1");
                            }
                        }

                    }

                    //设置相应的值
                    if (keyCode.equals("temperatureAdd") || keyCode.equals("temperatureReduce")) {
                        Integer value = Integer.parseInt(changeList.get(0).getValue().toString()) + Integer.parseInt(one.getValue());
                        if (value < 16) {
                            value = 16;
                        } else if (value > 30) {
                            value = 30;
                        }
                        changeList.get(0).setValue(value);
                    } else {
                        changeList.get(0).setValue(one.getValue());
                    }


                    //加上其他的参数
                    changeList.addAll(otherList);

                    //设置改变的thingModel
                    thingModel.setProperties(changeList);
                    //发送红外数据
                    if (keyCode.equals("temperature")) {
                        keyCode = "temperatureEq";
                    }
                } else if (one.getDeviceType().equals("净化灯")) {
                    if (keyCode.equals("close") || keyCode.equals("open")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("powerstate")).collect(Collectors.toList());
                    } else if (keyCode.equals("lampshadeOpen") || keyCode.equals("lampshadeClose")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("lampshade")).collect(Collectors.toList());
                    }else {
                        String key = keyCode;
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains(key)).collect(Collectors.toList());
                    }
                    thingModel.setProperties(changeList);
                } else {
                    if (keyCode.equals("close") || keyCode.equals("open")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("powerstate")).collect(Collectors.toList());
                    } else if (keyCode.equals("fanSwing")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("oscillatingswitch")).collect(Collectors.toList());
                    } else if (keyCode.equals("airdirection")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("fancategory")).collect(Collectors.toList());
                    } else if (keyCode.equals("fanspeedAdd") || keyCode.equals("fanspeedReduce")) {
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains("fanspeed")).collect(Collectors.toList());
                    } else {
                        String key = keyCode;
                        changeList = properties.stream().filter(it -> it.getIdentifier().contains(key)).collect(Collectors.toList());
                    }

                    //设置相应的值
                    if (keyCode.equals("fanspeedAdd") || keyCode.equals("fanspeedReduce")) {
                        Integer value = Integer.parseInt(changeList.get(0).getValue().toString()) + Integer.parseInt(one.getValue());
                        if (value > 3) {
                            value = 3;
                        } else if (value > 0) {
                            value = 0;
                        }
                        changeList.get(0).setValue(value);
                        keyCode = "fanspeed";
                    } else {
                        changeList.get(0).setValue(one.getValue());
                    }
                    thingModel.setProperties(changeList);
                }
                //保存识别记录
                speechRecordService.save(SpeechRecord.builder()
                        .deviceId(userDevice.getDeviceId())
                        .userId(userDevice.getUserId())
                        .homeId(userDevice.getHomeId())
                        .intentName("离线语音控制")
                        .text(one.getCnName())
                        .build());
                bizIrDeviceService.sendIrData(userDevice, thingModel, keyCode);
            }

        }

    }

}

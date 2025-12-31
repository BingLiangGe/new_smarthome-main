package com.lj.iot.api.hotel.web.open;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.aiui.core.dto.Answer;
import com.lj.iot.common.aiui.core.dto.AppSkillPostProcessorParams;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.IIdentifyingTextPostProcessor;
import com.lj.iot.common.aiui.core.service.ISkillPostProcessor;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.Sha1Utils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tyj
 * @date 2023-10-24 10:46:09
 * 酒店应用后处理
 */
@RestController
@Slf4j
@RequestMapping("/api/open/hotel/skill")
public class AppSkillPostHotelProcessorController {


    @Autowired
    private ISkillPostProcessor skillPostProcessor;

    @Autowired
    private IIdentifyingTextPostProcessor iIdentifyingTextPostProcessor;

    @Autowired
    private AiuiProperties properties;

    public IntentDto intentDto = new IntentDto();


    /*@RequestMapping("post_processor")
    public Object postProcessor(String params) {
        try {
            log.info("AppSkillPostProcessorController.postProcessor;{}", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Sha1Utils.getSha1("fe47715898cd5098");
    }*/


    @RequestMapping("post_processor")
    public Object postProcessor(@RequestBody String params) {
        Long begin = new Date().getTime();
        Object result = postProcessor2(params);
        iIdentifyingTextPostProcessor.handle(intentDto);
        log.info("=======post_processor======" + (new Date().getTime() - begin));
        return result;
    }


    private synchronized Object postProcessor2(String postBody) {
        log.info("AppSkillPostHotelProcessorController.postProcessor;{}", postBody);

        AppSkillPostProcessorParams params = JSON.parseObject(postBody, AppSkillPostProcessorParams.class);
        String userParams = params.getUserParams();
        //872处理起来很困难,协助872进行处理
        String str = new String(Base64Utils.decodeFromString(userParams), StandardCharsets.UTF_8);
        // String s = JSON.parse(str).toString();
        JSONObject userParamObj = JSON.parseObject(str);
        log.info("userParamObj-----------------------" + userParamObj.toJSONString());
        //IntentDto intentDto = new IntentDto();
        intentDto.setUserId(userParamObj.getString("userId"));
        intentDto.setMasterDeviceId(userParamObj.getString("deviceId"));

        String content = params.getMsg().getContent();
        JSONObject contentObj = JSON.parseObject(new String(Base64Utils.decodeFromString(content), StandardCharsets.UTF_8));
        log.info("contentObj-----------------------" + contentObj.toJSONString());
        JSONObject intent = contentObj.getJSONObject("intent");
        try {
            //识别语料
            String text = intent.getString("text");
            intentDto.setText(text);
            //iIdentifyingTextPostProcessor.handle(intentDto);

            if (StringUtil.isBlank(text)) {
               /* intentDto.setAnswer("我没听清,请再说一次");
                intent.put("answer", Answer.MSG("我没听清,请再说一次"));*/

                intentDto.setAnswer("");
                intent.put("answer", Answer.MSG(""));
                return contentObj;
            }

            if (intent.getString("category") == null) {

                if (intent.getString("service") != null) {

                    // OA语音
                    if ("iFlytekQA".equals(intent.getString("service")) || "video".equals(intent.getString("service"))) {

                        //如果有返回语音，直接返回
                        if (intent.getString("answer") != null && StringUtils.hasText(intent.getJSONObject("answer").getString("text"))) {
                            intentDto.setAnswer(intent.getJSONObject("answer").getString("text"));
                            intent.put("answer", Answer.MSG(intent.getJSONObject("answer").getString("text")));
                            return contentObj;
                        }
                    }
                }

                intentDto.setAnswer("");
                intent.put("answer", Answer.MSG(""));
                return contentObj;
            }

            // 天气处理
            if ("IFLYTEK.weather".equals(intent.getString("category"))) {
                intentDto.setAnswer("好的,没问题");
                intentDto.setIntentName("CONTROL");
                return contentObj;
            }

            // category -> OS14584510905.new_iot_skill
            //只处理支持意图  category -> OS8784213619.new_iot_skill2
            if (!properties.getSupportIntents().contains(intent.getString("category"))) {
                intentDto.setAnswer("");
                return contentObj;
            }

            if (!intent.getString("service").contains("OS14584510905")) {
                //如果有返回语音，直接返回
                if (intent.getString("answer") != null && StringUtils.hasText(intent.getJSONObject("answer").getString("text"))) {
                    intentDto.setAnswer(intent.getString("answer"));
                    intent.put("answer", intent.getString("answer"));
                    return contentObj;
                }

            }

            //是否命中自定义语料
            /*if (!"custom".equals(intent.getString("intentType"))) {
                return contentObj;
            }*/

            JSONArray jsonArray = intent.getJSONArray("semantic");

            //命中技能
            if (jsonArray == null) {
                return contentObj;
            }

            JSONObject semanticObj = jsonArray.getJSONObject(0);
            intentDto.setIntentName(semanticObj.getString("intent"));

            Map<String, IntentDto.Slot> map = new HashMap<>();
            JSONArray slotJSONArray = semanticObj.getJSONArray("slots");

            //官方技能
            if ("CONTROL".equals(intentDto.getIntentName())) {
                //必须存在device     insType ; attr、 attrValue ;attr、attrValue 不能多个，不然逻辑不好处理
                for (int i = 0; i < slotJSONArray.size(); i++) {
                    JSONObject slotJSONObject = slotJSONArray.getJSONObject(i);
                    IntentDto.Slot slot = new IntentDto.Slot();
                    String name = slotJSONObject.getString("name");
                    //*******特定设备不支持**********//
                    if (name.equals("device")) {
                        //特定设备不支持
                        if (slotJSONObject.getString("value").contains("锁")) {
                            intent.put("answer", Answer.MSG("不支持门锁设备的语音控制"));
                            intentDto.setAnswer("不支持门锁设备的语音控制");
                            return contentObj;
                        }
                    }
                    //*******特定设备不支持**********//
                    slot.setName(slotJSONObject.getString("name"));
                    slot.setNormValue(slotJSONObject.getString("normValue"));
                    slot.setValue(slotJSONObject.getString("value"));

                    if ("attr".equals(slot.getName())) {
                        IntentDto.Slot existSlot = map.get("attr");
                        if (existSlot != null) {
                            intent.put("answer", Answer.MSG("只能单个属性控制"));
                            intentDto.setAnswer("只能单个属性控制");
                            return contentObj;
                        }
                    }

                    if ("attrValue".equals(slot.getName())) {
                        IntentDto.Slot existSlot = map.get("attrValue");
                        if (existSlot != null) {
                            intent.put("answer", Answer.MSG("只能单个属性控制"));
                            intentDto.setAnswer("只能单个属性控制");
                            return contentObj;
                        }
                    }
                    map.put(slotJSONObject.getString("name"), slot);
                }

                IntentDto.Slot existSlot = map.get("device");


                if (existSlot == null) {

                    IntentDto.Slot attrSlot = map.get("attr");

                    if (map.get("attr").getNormValue().contains("模式")) {
                        log.info("进入场景");
                    } else if (attrSlot == null || !"mode".equals(attrSlot.getNormValue())) {
                        intent.put("answer", Answer.MSG("语音控制必须带上设备名称"));
                        intentDto.setAnswer("语音控制必须带上设备名称");
                        return contentObj;
                    }

                    //如果没有device  但有mode  可能命中场景
                    IntentDto.Slot sceneSlot = new IntentDto.Slot();
                    sceneSlot.setName("scene");
                    sceneSlot.setValue(text);
                    sceneSlot.setNormValue(text);
                    map.put("scene", sceneSlot);
                    intentDto.setIntentName("triggerScene");
                }
            } else {
                for (int i = 0; i < slotJSONArray.size(); i++) {
                    JSONObject slotJSONObject = slotJSONArray.getJSONObject(i);
                    IntentDto.Slot slot = new IntentDto.Slot();
                    slot.setName(slotJSONObject.getString("name"));
                    slot.setNormValue(slotJSONObject.getString("normValue"));
                    slot.setValue(slotJSONObject.getString("value"));
                    slot.setType("task");
                    map.put(slotJSONObject.getString("name"), slot);
                }
            }

            /* todo 要求空调离线在线都执行
            // 空调不处理
            if (Arrays.asList(AIR_CONTROL).contains(intentDto.getText())) {
                return null;
            }*/

            intentDto.setType("hotel");
            intentDto.setSlots(map);
            skillPostProcessor.handle(intentDto);
            boolean forHelp = intentDto.getIntentName().contains("forHelp");
            if (forHelp) {

                if (intentDto.getCallMsg() != null) {

                    if (intentDto.getCallMsg().contains("流控")) {
                        intent.put("answer", Answer.MSG("该被叫号触发被叫流控"));
                        intentDto.setAnswer("该被叫号触发被叫流控");
                        return contentObj;
                    } else if (intentDto.getCallMsg().contains("OK")) {
                        intent.put("answer", Answer.MSG("正在拨打紧急联系人电话"));
                        intentDto.setAnswer("正在拨打紧急联系人电话");
                        return contentObj;
                    } else {
                        intent.put("answer", Answer.MSG("电话拨打出错了,请重试一次"));
                        intentDto.setAnswer("电话拨打出错了,请重试一次");
                        return contentObj;
                    }
                }

                intent.put("answer", Answer.MSG("正在拨打紧急联系人电话"));
                intentDto.setAnswer("正在拨打紧急联系人电话");
                return contentObj;
            } else {

                String msg = "好的，没问题";
                if ("clock".equals(intentDto.getIntentName()) || "Chromophore".equals(intentDto.getIntentName())) {
                    msg = "好的," + intentDto.getAnswer();
                } else if ("GearPosition".equals(intentDto.getIntentName()) || "RisingAndFalling".equals(intentDto.getIntentName())) {
                    msg = intentDto.getAnswer();
                }

                intent.put("answer", Answer.MSG(msg));
                intentDto.setAnswer(msg);
                return contentObj;
            }

        } catch (CommonException e) {
            log.error("AppSkillPostProcessorController.postProcessor", e);
            intent.put("answer", Answer.MSG(e.getMsg()));
            intentDto.setAnswer(e.getMsg());
            return contentObj;
        } catch (Exception e) {
            boolean forHelp = intentDto.getIntentName().contains("forHelp");
            if (forHelp) {
                intent.put("answer", Answer.MSG("该被叫号触发被叫流控"));
                intentDto.setAnswer("该被叫号触发被叫流控");
                return contentObj;
            }
            log.error("AppSkillPostProcessorController.postProcessor", e);
            intent.put("answer", Answer.MSG("太累了，让我偷下懒"));
            intentDto.setAnswer("太累了，让我偷下懒");
            return contentObj;
        }
    }
}

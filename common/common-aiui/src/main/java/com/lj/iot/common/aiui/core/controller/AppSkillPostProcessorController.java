package com.lj.iot.common.aiui.core.controller;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 应用后处理
 *
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/app/skill")
public class AppSkillPostProcessorController {


   /* todo  要求空调离线在线都执行
    private static final String[] AIR_CONTROL = {"打开空调", "关闭空调", "空调制冷模式", "空调制热模式", "空调除湿模式", "空调通风模式", "空调自动模式", "空调低速风", "空调中速风",
            "空调高速风", "空调十六度", "空调十七度", "空调十八度", "空调十九度", "空调二十度", "空调二十一度", "空调二十二度", "空调二十三度", "空调二十四度", "空调二十五度", "空调二十六度", "空调二十七度",
            "空调二十八度", "空调二十九度", "空调三十度"};*/

    @Resource(name = "skillPostProcessor")
    private ISkillPostProcessor skillPostProcessor;

    @Autowired
    private IIdentifyingTextPostProcessor iIdentifyingTextPostProcessor;

    @Autowired
    private AiuiProperties properties;

    @Resource(name = "skillPostFoure")
    private ISkillPostProcessor skillFour;

    public IntentDto intentDto = new IntentDto();

   /* @RequestMapping("post_processor")
    public Object postProcessor( String params) {
        log.info("AppSkillPostProcessorController.postProcessor;{}", params);
        return Sha1Utils.getSha1("cbb3dd68ca8347b9");
    }*/

    /**
     * 应用后处理
     *
     * @param params
     * @return
     */
    @RequestMapping("post_processor")
    public Object postProcessor(@RequestBody String params) {
        Long begin = new Date().getTime();
        Object result = postProcessor2(params);
        iIdentifyingTextPostProcessor.handle(intentDto);
        log.info("=======post_processor======" + (new Date().getTime() - begin));
        return result;
    }

    private synchronized Object postProcessor2(String postBody) {
        log.info("AppSkillPostProcessorController.postProcessor;{}", postBody);

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
                intentDto.setAnswer("我没听清,请再说一次");
                intent.put("answer", Answer.MSG("我没听清,请再说一次"));
                return contentObj;
            }

            if (intent.getString("category") == null) {

                if (intent.getString("service") != null) {

                    // OA语音
                    if ("iFlytekQA".equals(intent.getString("service")) || "video".equals(intent.getString("service"))) {

                        // 情景模式丢失
                        if (text.length() < 10) {
                            skillFour.handle(intentDto);
                        }

                        //如果有返回语音，直接返回
                        if (intent.getString("answer") != null && StringUtils.hasText(intent.getJSONObject("answer").getString("text"))) {
                            intentDto.setAnswer(intent.getJSONObject("answer").getString("text"));
                            intent.put("answer", Answer.MSG(intent.getJSONObject("answer").getString("text")));
                            return contentObj;
                        }
                    }
                }

                intentDto.setAnswer("不支持意图");
                intent.put("answer", Answer.MSG("不支持意图"));
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
                intentDto.setAnswer("不支持意图");
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

    public static void main(String[] args) {
        String key = "eyJpbnRlbnQiOnsiYW5zd2VyIjp7ImFuc3dlclR5cGUiOiJMYXN0R3VhcmQiLCJlbW90aW9uIjoiZGVmYXVsdCIsInF1ZXN0aW9uIjp7InF1ZXN0aW9uIjoi5b+r54K55ZCD6aWtIiwicXVlc3Rpb25fd3MiOiLlv6vngrnlkIPppa0ifSwidGV4dCI6IuWlveeahO+8jOWTiOWTiCIsInRvcGljSUQiOiJOVUxMIiwidHlwZSI6IlQifSwibm9fbmx1X3Jlc3VsdCI6MCwib3BlcmF0aW9uIjoiQU5TV0VSIiwicmMiOjAsInNlcnZpY2UiOiJMYXN0R3VhcmQiLCJzZXJ2aWNlQ2F0ZWdvcnkiOiJMYXN0R3VhcmQiLCJzZXJ2aWNlTmFtZSI6Ikxhc3RHdWFyZCIsInNlcnZpY2VUeXBlIjoicHJldmVudGl2ZSIsInNpZCI6ImNpZGExNzEzMTYxQGR4MDAwYjE2YjE1MDA0MDEwMDEwIiwidGV4dCI6IuW/q+eCueWQg+mlrSIsInV1aWQiOiIifX0=";
        //String key ="eyJpbnRlbnQiOnsiY2F0ZWdvcnkiOiJJRkxZVEVLLnNtYXJ0Q29udHJvbFBybyIsImRpYWxvZ19zdGF0IjoiRGF0YVZhbGlkIiwicmMiOjAsInNhdmVfaGlzdG9yeSI6dHJ1ZSwic2VtYW50aWMiOlt7ImludGVudCI6IkNPTlRST0wiLCJzbG90cyI6W3sibmFtZSI6Imluc1R5cGUiLCJub3JtVmFsdWUiOiJ0dXJuT24iLCJ2YWx1ZSI6IuaJk+W8gCJ9LHsibmFtZSI6ImRldmljZSIsIm5vcm1WYWx1ZSI6ImFpckNvbnRyb2wiLCJ2YWx1ZSI6IuepuuiwgyJ9XX1dLCJzZXJ2aWNlIjoic21hcnRDb250cm9sUHJvIiwic2lkIjoiY2lkYTE3MTMxNjFAZHgwMDBiMTZiMTRlNzAwMTAwMGQiLCJzdGF0ZSI6eyJmZzo6c21hcnRDb250cm9sUHJvOjpkZWZhdWx0OjpkZWZhdWx0Ijp7InN0YXRlIjoiZGVmYXVsdCJ9fSwidGV4dCI6IuaJk+W8gOepuuiwgyIsInVzZWRfc3RhdGUiOnsic3RhdGUiOiJkZWZhdWx0Iiwic3RhdGVfa2V5IjoiZmc6OnNtYXJ0Q29udHJvbFBybzo6ZGVmYXVsdDo6ZGVmYXVsdCJ9LCJ1dWlkIjoiY2lkYTE3MTMxNjFAZHgwMDBiMTZiMTRlNzAwMTAwMGQiLCJ2ZXJzaW9uIjoiNzQ3LjAifX0=";
        //String key = "eyJkZXZpY2VJZCI6IjE1NzE3NzM5NDg3ODY3ODIyMTEiLCJ1c2VySWQiOiIyMDIyMDkyMjExNTQ0NDYyNTI5ODkzOTEyMTU1NzUwNCJ9";
        // String key = "eyJpbnRlbnQiOnsiYW5zd2VyIjp7InRleHQiOiLlpb3lg4/lh7rkuobngrnpl67popjvvIznqI3lkI7lho3or5Xor5XlkKfjgIIiLCJ0eXBlIjoiVCJ9LCJjYXRlZ29yeSI6Ik9TMTQ1ODQ1MTA5MDUubmV3X2lvdF9za2lsbCIsImRhdGEiOnsicmVzdWx0IjpudWxsfSwiaW50ZW50VHlwZSI6ImN1c3RvbSIsInJjIjowLCJzZW1hbnRpYyI6W3siZW50cnlwb2ludCI6ImVudCIsImhhemFyZCI6ZmFsc2UsImludGVudCI6ImRldmljZVN3aXRjaCIsInNjb3JlIjoxLCJzbG90cyI6W3siYmVnaW4iOjAsImVuZCI6MiwibmFtZSI6InN3aXRjaCIsIm5vcm1WYWx1ZSI6Im9wZW4iLCJ2YWx1ZSI6IuaJk+W8gCJ9LHsiYmVnaW4iOjIsImVuZCI6NCwibmFtZSI6ImRldmljZU5hbWUiLCJub3JtVmFsdWUiOiLnqbrosIMiLCJ2YWx1ZSI6IuepuuiwgyJ9XSwidGVtcGxhdGUiOiJ7c3dpdGNofXtkZXZpY2VOYW1lfSJ9XSwic2VtYW50aWNUeXBlIjowLCJzZXJ2aWNlIjoiT1MxNDU4NDUxMDkwNS5uZXdfaW90X3NraWxsIiwic2Vzc2lvbklzRW5kIjp0cnVlLCJzaG91bGRFbmRTZXNzaW9uIjp0cnVlLCJzaWQiOiJjaWRhMTcyY2I2OUBkeDAwMGIxNmIxNDFjOTAxMDAwNSIsInN0YXRlIjpudWxsLCJ0ZXh0Ijoi5omT5byA56m66LCDIiwidXVpZCI6ImNpZGExNzJjYjY5QGR4MDAwYjE2YjE0MWM5MDEwMDA1IiwidmVuZG9yIjoiT1MxNDU4NDUxMDkwNSIsInZlcnNpb24iOiI5LjAiLCJ2b2ljZV9hbnN3ZXIiOlt7ImNvbnRlbnQiOiLlpb3lg4/lh7rkuobngrnpl67popjvvIznqI3lkI7lho3or5Xor5XlkKfjgIIiLCJ0eXBlIjoiVFRTIn1dfX0=";
        String a = new String(Base64.getDecoder().decode(key));
        System.out.println(a);
    }
}

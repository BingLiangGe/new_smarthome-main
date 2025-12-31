package com.lj.iot.common.aiui.core.controller;

import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.aiui.core.service.ISkillPostProcessor;
import com.lj.iot.common.aiui.core.vo.IntentResult;
import com.lj.iot.common.base.enums.CommonCodeEnum;
import com.lj.iot.common.base.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 技能后置处理器
 *
 * @author mz
 * @Date 2022/8/9
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/open/skill")
public class SkillPostProcessorController {

    @Autowired
    private AiuiProperties properties;
    @Autowired
    private ISkillPostProcessor skillPostProcessor;

    /**
     * 技能后处理
     *
     * @param reqBody
     * @param request
     * @return
     */
    @RequestMapping("post_processor")
    public IntentResult postProcessor(@RequestBody String reqBody, HttpServletRequest request) {
        log.info("SkillPostProcessorController.postProcessor;{}", reqBody);
        //验签
        String signature = request.getHeader("Signature");

        /*if (!verify(reqBody, signature)) {
            return IntentResult.MSG("验签不通过");
        }*/

        try {
            JSONObject jsonObject = JSON.parseObject(reqBody);
            JSONObject ifrequest = jsonObject.getJSONObject("request");
            if (ifrequest == null) {
                return IntentResult.MSG("太累了，让我偷下懒");
            }

            String type = ifrequest.getString("type");
            switch (type) {
                case "IntentRequest":
                    IntentDto intentDto = intentRequest(jsonObject);
                    skillPostProcessor.handle(intentDto);
                    break;
                default:
                    return IntentResult.MSG("请求不匹配");
            }

        } catch (CommonException e) {
            log.error("SkillPostProcessor.postProcessor:", e);
            if (e.getCode().equals(CommonCodeEnum.KEEP_SESSION.getCode())) {
                IntentResult intentResult = IntentResult.MSG(e.getMsg());
                intentResult.getResponse().withExpectSpeech(true);
                log.info("SkillPostProcessor.postProcessor.intentResult:{}", JSON.toJSONString(intentResult));
                return intentResult;
            }
            return IntentResult.MSG(e.getMsg());
        } catch (Exception e) {
            log.error("SkillPostProcessor.postProcessor:", e);
            return IntentResult.MSG("太累了，让我偷下懒。");
        }

        return IntentResult.MSG("好的");
    }

    private IntentDto intentRequest(JSONObject body) {
        IntentDto intentDto = IntentDto.builder().build();

        /**
         * 获取deviceId
         * {
         *  "context": {
         *     "Custom": {
         *       "iflytek_data": {
         *         "user_data": "eyJkZXZpY2VJZCI6IjcwODlmNTAwMDAyZCIsInVzZXJJZCI6IjIifQ=="
         *         ...
         *         }
         *         ...
         *         },
         *         ...
         *       },
         *  ...
         * }
         */
        Optional.ofNullable(body.getJSONObject("context"))
                .flatMap(context -> Optional.ofNullable(context.getJSONObject("Custom")))
                .flatMap(custom -> Optional.ofNullable(custom.getJSONObject("iflytek_data")))
                .flatMap(iflytekData -> Optional.ofNullable(iflytekData.getString("user_data")))
                .ifPresent(userData -> {
                    JSONObject jsonObject = JSON.parseObject(new String(Base64.getDecoder().decode(userData)));
                    intentDto.setMasterDeviceId(jsonObject.getString("deviceId"));
                    intentDto.setUserId(jsonObject.getString("userId"));
                });


        /**
         *{
         *  "request": {
         *     "type":"IntentRequest",
         *     "intent": {
         *       "name":"xxx",//意图名称
         *       "confirmationStatus":"",//意图确认状态。取值：NONE未确认，CONFIRMED确认，DENIED否认。当dialogState为COMPLETED时，此处状态为CONFIRMED或DENIED。当意图不需要确认时，该字段不出现
         *       "slots":{//槽位，可能为空 意图中的槽位信息，以key-value结构展示，key为槽名，value为槽值。只显示解析出来的槽。IVS开放意图此项不显示；意图中未定义词槽，该字段不出现
         *           “xxx”:{
         *             "name":"",//槽位名称
         *             "value":"",//用户语料经过语义理解后解析出来的槽值，是用户语料中包含的词，如“周董”。
         *             "normValue":"",//用户语料经过语义理解后解析出来并规整后的槽值，是用户语料中包含的词，如“周杰伦”。可能会和value的值一样
         *             "moreValue":[],//用户语料经过语义理解后解析出来的槽值，当用户语料中包含该词槽的多个槽值时，出现该字段，以数组形式展示。比如用户说“我要买苹果，香蕉和橘子”，则value="苹果",moreValue=["香蕉","橘子"]。moreValue取值来源于用户语料中包含的槽值的词条名（normValue）
         *           }
         *       }
         *         ...
         *       },
         *       ...
         *     },
         *  ...
         * }
         */
        Optional.ofNullable(body.getJSONObject("request"))
                .flatMap(request -> Optional.ofNullable(request.getJSONObject("intent")))
                .ifPresent(intent -> {
                    intentDto.setIntentName(intent.getString("name"));

                    JSONObject slots = intent.getJSONObject("slots");
                    Map<String, IntentDto.Slot> slotMap = new HashMap<>();
                    if (slots != null) {
                        for (String s : slots.keySet()) {
                            slotMap.put(s, JSON.parseObject(slots.getString(s), IntentDto.Slot.class));
                        }
                    }
                    intentDto.setSlots(slotMap);
                });
        return intentDto;
    }

    private boolean verify(String body, String signature) {
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, null, Base64.getDecoder().decode(properties.getPublicKey()));
        return sign.verify(DigestUtils.sha1Hex(body).getBytes(StandardCharsets.UTF_8), Base64.getDecoder().decode(signature));
    }

    public static boolean verify1(String body, String signature, String pk) {
        Sign sign = new Sign(SignAlgorithm.SHA256withRSA, null, Base64.getDecoder().decode(pk));
        return sign.verify(DigestUtils.sha1Hex(body).getBytes(StandardCharsets.UTF_8), Base64.getDecoder().decode(signature));
    }

    public static void main(String[] args) {
        String pk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSYuhwRZI9d3z09OnEhOP2P80n" +
                "ANszAOXrJhUm8ZI1kPXtuz1qBTE5p8Hqzyl7vAy+jqhpf4eIZplvssxxde8tUWW/" +
                "8IUvVKTVFH9gaToIblv/o+cejq5t+3IglO6UE06DXAxCLdYx6lKBepXAUEh2eQNR" +
                "W9P+UPH/7XeyWLN5ywIDAQAB";
        //System.out.println(new String(Base64.getDecoder().decode("eyJkZXZpY2VJZCI6IjE1NjQ4MjEwNjQyNTgwNTYxOTMiLCJ1c2VySWQiOiIyMDIyMDgzMTE0MDAwOTYxNzM1Nzk2OTI4NTM5NDQzMiJ9")));
        String a = "{\"version\":\"2.1\",\"context\":{\"AudioPlayer\":\"\",\"Custom\":{\"iflytek_data\":{\"user_data\":\"eyJkZXZpY2VJZCI6IjE1NjQ4MjEwNjQyNTgwNTYxOTMiLCJ1c2VySWQiOiIyMDIyMDgzMTE0MDAwOTYxNzM1Nzk2OTI4NTM5NDQzMiJ9\"}},\"System\":{\"application\":{\"applicationId\":\"OS10286212306.test_smartHome\",\"enable\":true},\"device\":{\"deviceId\":\"d18204764472\",\"location\":{},\"supportedInterfaces\":null},\"user\":{\"accessToken\":\"\",\"userId\":\"d18204764472\"}}},\"session\":{\"new\":false,\"sessionId\":\"7cb461d8-5856-434e-ad9e-8af9b5379575\"},\"request\":{\"type\":\"IntentRequest\",\"requestId\":\"cida15ff64d@dx000b166a55b6010022\",\"timestamp\":\"2022-08-31T15:11:53.475Z\",\"dialogState\":\"STARTED\",\"query\":{\"type\":\"TEXT\",\"original\":\"射灯测试亮度\"},\"intent\":{\"name\":\"SET\",\"score\":1,\"confirmationStatus\":\"NONE\",\"slots\":{\"device\":{\"confirmationStatus\":\"NONE\",\"moreValue\":null,\"name\":\"device\",\"normValue\":\"SpotLight\",\"value\":\"射灯\"},\"poetn\":{\"confirmationStatus\":\"NONE\",\"moreValue\":null,\"name\":\"poetn\",\"normValue\":\"亮度\",\"value\":\"亮度\"}}}}}";
        String b = "KVrjCRFlBY6mRAbzAhQqGOCqk/iGUF7YHHO/7W3WOzpJpBIWjCe7w3/k3xn3RQyHsSiviV1Tbb6Pu7sBB8pgSgmZZ0eWA5uDbJgpwro3VwaH0f7BztE9i3+DOIiJk3fXh6KM9sKS2VamEeUdFBPJzZmSPOL9jCm1LO/KYR7it/I=";
        System.out.println(verify1(a, b, pk));
    }
}

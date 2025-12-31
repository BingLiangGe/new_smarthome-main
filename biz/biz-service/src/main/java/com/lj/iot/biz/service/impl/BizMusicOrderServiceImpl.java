package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.MusicOrderStatusEnum;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.lj.iot.biz.db.smart.service.IMusicOrderService;
import com.lj.iot.biz.service.BizMusicOrderService;
import com.lj.iot.common.aiui.core.properties.AiuiProperties;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.MD5Utils;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BizMusicOrderServiceImpl implements BizMusicOrderService {

    @Autowired
    private IMusicOrderService musicOrderService;

    @Autowired
    private AiuiProperties properties;

    @Override
    public void completeOrder(String orderNo, String transactionId) {
        MusicOrder musicOrder = musicOrderService.getOne(new QueryWrapper<>(MusicOrder.builder()
                .orderNo(orderNo)
                .build()));

        if (musicOrder == null) {
            log.error("WeChatController.completeOrder音乐订单订单不存在orderNo={},transactionId={}", orderNo, transactionId);
            return;
        }
        musicOrderService.update(MusicOrder.builder()
                .state(MusicOrderStatusEnum.SUCCESS.getCode())
                .transactionId(transactionId)
                .prepayId(transactionId)
                .build(), new QueryWrapper<>(MusicOrder.builder()
                .id(musicOrder.getId())
                .state(MusicOrderStatusEnum.UN_PAY.getCode())
                .build()));
        //音乐激活逻辑
        active(musicOrder.getDeviceId(), musicOrder.getUserId());
    }

    @Override
    public void active(String deviceId, String userId) {

        String appId = properties.getAppId();
        String appKey = properties.getAppKey();

        Long timestamp = new Date().getTime();
        Map map = new HashMap<String, String>();
        map.put("appId", appId);
        map.put("timestamp", timestamp + "");
        map.put("token", MD5Utils.standardSign(appId + appKey + timestamp));
        map.put("userId", userId);
        map.put("serialNumber", deviceId);
        map.put("deviceModel", "MASTER");

        try {
            String result = OkHttpUtils.post("https://adf.xfyun.cn/kuwo/active", map);
            log.info("BizMusicOrderServiceImpl.active" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            if (!jsonObject.getInteger("code").equals(200)) {
                throw CommonException.FAILURE(jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            log.error("BizMusicOrderServiceImpl.active", e);
        }
    }


    public static void main(String[] args) {
        String appId = "f8ab94dd";
        String appKey = "9cabc6200ddbb2cfab9678ef7d2e1e6f";
        String deviceId="998ca00a882c0b001";

        Long timestamp = new Date().getTime();
        Map map = new HashMap<String, String>();
        map.put("appId", appId);
        map.put("timestamp", timestamp + "");
        map.put("token", MD5Utils.standardSign(appId + appKey + timestamp));
        map.put("serialNumber", deviceId);

        System.out.println(map);
    }
}

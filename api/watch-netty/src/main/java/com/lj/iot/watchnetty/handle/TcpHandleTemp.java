package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.fegin.websocket.WsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 健康数据-体温
 *
 * @author tyj
 */
@Slf4j
@Component("tcpHandle_temp")
public class TcpHandleTemp implements TcpHandle {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IWatchHealthService watchHealthService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Autowired
    private IWatchSettingService watchSettingService;

    @Autowired
    private IWatchSosService watchSosService;

    @Resource
    ISystemMessagesService systemMessagesService;

    @Autowired
    private VmsProperties vmsProperties;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IFenceNoticeService fenceNoticeService;

    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("temp={}", userDevice);

        String[] data = dataMap.get("data").split(",");

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "temp");
        respJson.put("temperature", new BigDecimal(data[1]));

        double value = Double.parseDouble(data[1]);

        WatchSetting lowSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                .settingType(1)
                .deviceId(userDevice.getDeviceId())
                .dataType(3)
                .valueType(1).build()));

        WatchSetting highSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                .settingType(1)
                .deviceId(userDevice.getDeviceId())
                .dataType(3)
                .valueType(0).build()));

        if (lowSetting != null) {

            if (value < Double.valueOf(lowSetting.getSettingValue())) {
                watchSosService.save(WatchSos.builder()
                        .createTime(LocalDateTime.now())
                        .sosMobile(value + "")
                        .deviceId(userDevice.getDeviceId())
                        .sosType(3)
                        .build());

                systemMessagesService.save(SystemMessages.builder()
                        .readType(0)
                        .type(5)
                        .messages("体温低于设置值!测量值:" + value + ",设置值:" + lowSetting.getSettingValue())
                        .userId(userDevice.getUserId())
                        .inType("健康数据报警")
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now()).build());

                UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                    // 拨打电话
                    phoneCall(userAccount.getMobile(), userDevice.getCustomName(),"体温","低",String.valueOf(value));
                    // 保存通知记录
                    fenceNoticeService.save(FenceNotice.builder()
                            .deviceId(userDevice.getDeviceId())
                            .createTime(LocalDateTime.now())
                            .type(1).build());
                }
            }
        }

        if (highSetting != null) {

            if (value > Double.valueOf(highSetting.getSettingValue())) {
                watchSosService.save(WatchSos.builder()
                        .createTime(LocalDateTime.now())
                        .sosMobile(value + "")
                        .deviceId(userDevice.getDeviceId())
                        .sosType(3)
                        .build());

                systemMessagesService.save(SystemMessages.builder()
                        .readType(0)
                        .type(5)
                        .messages("体温高于设置值!测量值:" + value + ",设置值:" + highSetting.getSettingValue())
                        .userId(userDevice.getUserId())
                        .inType("健康数据报警")
                        .createTime(LocalDateTime.now())
                        .updateTime(LocalDateTime.now()).build());

                UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                    // 拨打电话
                    phoneCall(userAccount.getMobile(), userDevice.getCustomName(),"体温","高",String.valueOf(value));
                    // 保存通知记录
                    fenceNoticeService.save(FenceNotice.builder()
                            .deviceId(userDevice.getDeviceId())
                            .createTime(LocalDateTime.now())
                            .type(1).build());
                }
            }
        }


        List<String> userIds = Lists.newArrayList();
        userIds.add(userDevice.getUserId());

        wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));

        watchHealthService.save(WatchHealth.builder()
                .deviceId(userDevice.getDeviceId())
                .createTime(LocalDateTime.now())
                .healthValue(data[1])
                .healthType(3)
                .build());

        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .temperature(new BigDecimal(data[1]))
                .build());

        return null;
    }


    public void phoneCall(String mobile, String deviceName, String dataType, String height, String value) {
        JSONObject params = new JSONObject();
        String ttsCode = "TTS_286975178";
        params.put("deviceName", deviceName);
        params.put("dataType", dataType);
        params.put("height", height);
        params.put("value", value);

        Config config = new Config()
                .setEndpoint(vmsProperties.getDomain())
                .setRegionId(vmsProperties.getRegionId())
                .setAccessKeyId(vmsProperties.getAccessKeyId())
                .setAccessKeyCCCFDF(vmsProperties.getAccessCCCFDF());

        final SingleCallByTtsRequest request = new SingleCallByTtsRequest()
                .setTtsCode(ttsCode)
                .setCalledNumber(mobile)
                .setCalledShowNumber(vmsProperties.getCalledShowNumber())
                .setTtsParam(JSON.toJSONString(params));
        try {
            Client client = new Client(config);
            final SingleCallByTtsResponse response = client.singleCallByTts(request);
            log.info("智能手表响应结果.call: {}", JSON.toJSONString(response));

        } catch (Exception e) {
            log.error("智能手表.call呼叫失败", e);
        }
    }
}

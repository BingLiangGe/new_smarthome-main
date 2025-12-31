package com.lj.iot.watchnetty.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.biz.base.vo.WsResultVo;
import com.lj.iot.biz.db.smart.entity.FenceNotice;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IFenceNoticeService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.sms.service.ISmsService;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.common.util.util.MapUtil;
import com.lj.iot.fegin.websocket.WsFeignClient;
import com.lj.iot.watchnetty.server.BootNettyChannelInboundHandlerAdapter;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 位置
 *
 * @author tyj
 */
@Slf4j
@Component("tcpHandle_UD")
public class TcpHandleUD implements TcpHandle {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Resource
    private WsFeignClient wsFeignClient;

    @Resource(name = "SmsServiceImpl")
    private ISmsService smsService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IFenceNoticeService fenceNoticeService;

    @Autowired
    private VmsProperties vmsProperties;


    @Override
    public String handle(UserDevice userDevice, Map<String, String> dataMap) {
        log.info("ud={}", userDevice);

        String address = null;
        try {

            if ("V".equals(dataMap.get("dataType")) && StringUtil.isNotBlank(dataMap.get("wifiMac"))) {
                String latAndLng = OkHttpUtils.get("http://apilocate.amap.com/position?key=1cb2a22264d4908ee289466a32be5286&accesstype=1&macs=" + dataMap.get("wifiMac"));
                JSONObject respJsonLatAndLng = JSONObject.parseObject(latAndLng).getJSONObject("result");
                String desc = respJsonLatAndLng.getString("desc");

                if (StringUtil.isNotBlank(desc)) {
                    String respData = OkHttpUtils.get("https://restapi.amap.com/v3/geocode/regeo?key=1cb2a22264d4908ee289466a32be5286&location=" + respJsonLatAndLng.getString("location"));
                    JSONObject respJson = JSONObject.parseObject(respData);

                    // 成功获取地址信息
                    dataMap.put("longitude", respJsonLatAndLng.getString("location").split(",")[0]);
                    dataMap.put("latitude", respJsonLatAndLng.getString("location").split(",")[1]);
                    address = respJson.getJSONObject("regeocode").getString("formatted_address");
                }
            } else if ("A".equals(dataMap.get("dataType"))) {
                String latAndLng = OkHttpUtils.get("https://restapi.amap.com/v3/assistant/coordinate/convert?key=1cb2a22264d4908ee289466a32be5286&coordsys=gps&locations=" + dataMap.get("longitude") + "," + dataMap.get("latitude"));

                JSONObject respJsonLatAndLng = JSONObject.parseObject(latAndLng);

                dataMap.put("longitude", respJsonLatAndLng.getString("locations").split(",")[0]);
                dataMap.put("latitude", respJsonLatAndLng.getString("locations").split(",")[1]);

                String respData = OkHttpUtils.get("https://restapi.amap.com/v3/geocode/regeo?key=1cb2a22264d4908ee289466a32be5286&location=" + respJsonLatAndLng.getString("locations"));

                JSONObject respJson = JSONObject.parseObject(respData);

                // 成功获取地址信息
                if ("OK".equals(respJson.getString("info"))) {
                    JSONObject addressJson = respJson.getJSONObject("regeocode").getJSONObject("addressComponent");

                    JSONObject streetJson = addressJson.getJSONObject("streetNumber");
                    if (streetJson != null) {
                        address = addressJson.getString("province") + addressJson.getString("city") + addressJson.getString("township") + streetJson.getString("street") + streetJson.getString("number");
                    } else {
                        address = respJson.getJSONObject("regeocode").getString("formatted_address");
                    }
                }
            } else {
                String latAndLng = OkHttpUtils.get("http://apilocate.amap.com/position?key=1cb2a22264d4908ee289466a32be5286&accesstype=1&bts=" + dataMap.get("lbs"));
                JSONObject respJsonLatAndLng = JSONObject.parseObject(latAndLng).getJSONObject("result");
                String desc = respJsonLatAndLng.getString("desc");

                if (StringUtil.isNotBlank(desc)) {
                    dataMap.put("longitude", respJsonLatAndLng.getString("location").split(",")[0]);
                    dataMap.put("latitude", respJsonLatAndLng.getString("location").split(",")[1]);
                    address = desc;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject respJson = new JSONObject();
        respJson.put("deviceId", userDevice.getDeviceId());
        respJson.put("type", "ud");
        respJson.put("longitude", dataMap.get("longitude"));
        respJson.put("latitude", dataMap.get("latitude"));

        List<String> userIds = Lists.newArrayList();
        userIds.add(userDevice.getUserId());

        wsFeignClient.appSend(WsResultVo.FAILURE(userIds, null, RedisTopicConstant.TOPIC_CHANNEL_WATCH_DATA, null, respJson.toJSONString()));


        // 设置围栏
        if (userDevice.getSettingLat() != null && userDevice.getSettingLng() != null) {
            // 未在围栏内
            if (!MapUtil.isInRange(userDevice.getRadius(), new BigDecimal(userDevice.getSettingLat()), new BigDecimal(userDevice.getSettingLng()), new BigDecimal(dataMap.get("latitude")), new BigDecimal(dataMap.get("longitude")))) {
                exitFence(userDevice, dataMap);
            }
        }


        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .longitude(dataMap.get("longitude"))
                .address(address)
                .latitude(dataMap.get("latitude")).build());

        return null;
    }


    public void exitFence(UserDevice userDevice, Map<String, String> dataMap) {
        BootNettyChannelInboundHandlerAdapter.sendMsg(userDevice.getDeviceId(), "[" + "DW*" + userDevice.getDeviceId() + "*0004*FIND" + "]");

        UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
        ValidUtils.isNullThrow(userAccount, "出围栏报警用户信息不可为空");

        if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(),0) <= 5) {
            // 发送短信
            smsService.send(userAccount.getMobile(), "SMS_463673519", Map.of("name", userDevice.getCustomName()));
            // 拨打电话
            phoneCall(userAccount.getMobile(), userDevice.getCustomName());
            // 保存通知记录
            fenceNoticeService.save(FenceNotice.builder()
                    .deviceId(userDevice.getDeviceId())
                    .createTime(LocalDateTime.now())
                            .type(0)
                    .lat(dataMap.get("latitude"))
                    .lng(dataMap.get("longitude")).build());
        }
    }


    public void phoneCall(String mobile, String deviceName) {
        JSONObject params = new JSONObject();
        String ttsCode = "TTS_287805065";
        params.put("deviceName", deviceName);

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

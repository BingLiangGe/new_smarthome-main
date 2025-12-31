package com.lj.iot.biz.db.smart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dyvmsapi20170525.Client;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsRequest;
import com.aliyun.dyvmsapi20170525.models.SingleCallByTtsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.vo.WatchSosVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.mapper.WatchSosMapper;
import com.lj.iot.biz.db.smart.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.commom.vms.properties.VmsProperties;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 手表sos记录 服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@Slf4j
@DS("smart")
@Service
public class WatchSosServiceImpl extends ServiceImpl<WatchSosMapper, WatchSos> implements IWatchSosService {


    @Autowired
    private IWatchSettingService watchSettingService;
    @Resource
    private WatchSosMapper mapper;

    @Resource
    ISystemMessagesService systemMessagesService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private VmsProperties vmsProperties;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IFenceNoticeService fenceNoticeService;

    @Override
    public void daring(String deviceId, Integer type, String value) {

        String typeName = null;

        switch (type) {
            case 0:
                typeName = "血压";
                break;
            case 1:
                typeName = "血氧";
                break;
            case 2:
                typeName = "心率";
                break;
            case 3:
                typeName = "体温";
                break;
        }

        WatchSetting lowSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                .settingType(1)
                .deviceId(deviceId)
                .dataType(type)
                .valueType(1).build()));

        UserDevice userDevice = userDeviceService.getById(deviceId);

        ValidUtils.isNullThrow(userDevice, "设备不存在!");

        WatchSetting highSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                .settingType(1)
                .deviceId(deviceId)
                .dataType(type)
                .valueType(0).build()));

        if (lowSetting != null) {

            // 血压特殊处理
            if (type == 0) {
                Integer low = Integer.valueOf(value.split(",")[1]);
                Integer high = Integer.valueOf(value.split(",")[0]);

                Integer settingLow = Integer.valueOf(lowSetting.getSettingValue().split(",")[1]);
                Integer settingHign = Integer.valueOf(lowSetting.getSettingValue().split(",")[0]);

                if (low < settingLow) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(low + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "低于设置值!测量值:" + low + ",设置值:" + settingLow)
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "低", String.valueOf(low));
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }

                if (high < settingHign) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(high + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "高于设置值!测量值:" + high + ",设置值:" + settingHign)
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "高", String.valueOf(high));
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }

            } else {
                if (Integer.valueOf(value) < Integer.valueOf(lowSetting.getSettingValue())) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(value + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "低于设置值!测量值:" + value + ",设置值:" + lowSetting.getSettingValue())
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "低", value);
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }
            }
        }

        if (highSetting != null) {

            // 血压特殊处理
            if (type == 0) {
                Integer low = Integer.valueOf(value.split(",")[1]);
                Integer high = Integer.valueOf(value.split(",")[0]);

                Integer settingLow = Integer.valueOf(highSetting.getSettingValue().split(",")[1]);
                Integer settingHign = Integer.valueOf(highSetting.getSettingValue().split(",")[0]);

                if (low > settingLow) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(low + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "低于设置值!测量值:" + low + ",设置值:" + settingLow)
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "低", String.valueOf(low));
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }

                if (high > settingHign) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(high + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "高于设置值!测量值:" + high + ",设置值:" + settingHign)
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "高", String.valueOf(high));
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }
            } else {
                if (Integer.valueOf(value) > Integer.valueOf(highSetting.getSettingValue())) {
                    save(WatchSos.builder()
                            .createTime(LocalDateTime.now())
                            .sosMobile(value + "")
                            .deviceId(deviceId)
                            .sosType(type)
                            .build());

                    systemMessagesService.save(SystemMessages.builder()
                            .readType(0)
                            .type(5)
                            .messages(typeName + "高于设置值!测量值:" + value + ",设置值:" + highSetting.getSettingValue())
                            .userId(userDevice.getUserId())
                            .inType("健康数据报警")
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now()).build());

                    UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
                    ValidUtils.isNullThrow(userAccount, "健康数据报警信息不可为空");

                    if (fenceNoticeService.selectTodayNumber(userDevice.getDeviceId(), 1) <= 5) {
                        // 拨打电话
                        phoneCall(userAccount.getMobile(), userDevice.getCustomName(), typeName, "高", value);
                        // 保存通知记录
                        fenceNoticeService.save(FenceNotice.builder()
                                .deviceId(userDevice.getDeviceId())
                                .createTime(LocalDateTime.now())
                                .type(1).build());
                    }
                }
            }
        }
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

    @Override
    public List<WatchSosVo> getSosList(String deviceId, String date, Integer type) {
        return mapper.getSosList(deviceId, date, type);
    }
}

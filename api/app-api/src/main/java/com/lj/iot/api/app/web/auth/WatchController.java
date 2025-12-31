package com.lj.iot.api.app.web.auth;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.dto.WatchMobileDto;
import com.lj.iot.biz.base.dto.WatchMsgDto;
import com.lj.iot.biz.base.dto.WatchSaveDto;
import com.lj.iot.biz.base.vo.WatchChartsVo;
import com.lj.iot.biz.base.vo.WatchInfoVo;
import com.lj.iot.biz.base.vo.WatchSettingInfoVo;
import com.lj.iot.biz.base.vo.WatchSosVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizWatchPublishService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.file.WatchEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 手表控制器
 *
 * @author tyj
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/auth/watch_device")
public class WatchController {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private BizWatchPublishService watchPublishService;

    @Autowired
    private IWatchMobileService watchMobileService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IPhonebookService phonebookService;

    @Autowired
    private IFenceSettingService fenceSettingService;

    @Autowired
    private IWatchHealthService watchHealthService;

    @Autowired
    private IWatchSettingService watchSettingService;

    @Autowired
    private IWatchSosService watchSosService;


    /**
     * 设置手表围栏
     *
     * @param deviceId
     * @param radius
     * @return
     */
    @RequestMapping("/settingWatchRadius")
    public CommonResultVo<String> settingWatchRadius(String deviceId, Integer radius) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(radius, "radius 必传");

        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备不存在");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        userDeviceService.updateById(UserDevice.builder()
                .deviceId(deviceId)
                .radius(radius).build());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 获取手表报警记录
     *
     * @param deviceId
     * @param date
     * @param type
     * @return
     */
    @RequestMapping("/watchDaringList")
    public CommonResultVo<List<WatchSosVo>> watchDaringList(String deviceId, String date, Integer type) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(type, "type 必传");
        ValidUtils.isNullThrow(date, "date 必传");
        return CommonResultVo.SUCCESS(watchSosService.getSosList(deviceId, date, type));
    }


    /**
     * 手表图表数据
     *
     * @param deviceId
     * @param date
     * @param type
     * @param dataType
     * @return
     */
    @RequestMapping("/watchChart")
    public CommonResultVo<List<WatchChartsVo>> watchChart(String deviceId, String date, Integer type, Integer dataType) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(type, "type 必传");
        ValidUtils.isNullThrow(date, "date 必传");
        ValidUtils.isNullThrow(dataType, "dataType 必传");

        List<WatchChartsVo> list = watchHealthService.selectChartData(deviceId, date, type, dataType);
        return CommonResultVo.SUCCESS(list);
    }

    /**
     * 设置手表详情
     *
     * @param deviceId
     * @param type
     * @return
     */
    @RequestMapping("/settingWatchInfo")
    public CommonResultVo<WatchSettingInfoVo> settingWatchInfo(String deviceId, Integer type) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(type, "type 必传");
        return CommonResultVo.SUCCESS(watchSettingService.getWathSettingByDeviceIdAndType(deviceId, type));
    }


    /**
     * 设置手表
     *
     * @param deviceId  设备号
     * @param timeValue 时间间隔
     * @param lowData   最低值
     * @param highData  最高值
     * @param type      类型  0血压 1血氧 2心率  3体温
     * @return
     */
    @RequestMapping("/settingWatch")
    public CommonResultVo<String> settingWatch(String deviceId, Integer timeValue, String lowData, String highData, Integer type) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(timeValue, "timeValue 必传");
        ValidUtils.isNullThrow(type, "type 必传");

        log.info("timeValue={}", timeValue);

        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备不存在");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        if (timeValue <= 0) {
            ValidUtils.isNullThrow(null, "时间间隔必须设置");
        }

        // 时间间隔
        WatchSetting timeSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                .settingType(0)
                .deviceId(deviceId)
                .dataType(type).build()));

        if (timeSetting == null) {
            timeSetting = WatchSetting.builder()
                    .settingType(0)
                    .dataType(type)
                    .createTime(LocalDateTime.now())
                    .deviceId(deviceId).build();
        }

        timeSetting.setSettingValue(timeValue.toString());
        watchSettingService.saveOrUpdate(timeSetting);


        watchPublishService.publish(WatchMsgDto.builder()
                .deviceId(deviceId)
                .data(sendWatch(deviceId, timeValue, type)).build());


        // 低值报警
        if (StringUtil.isNotBlank(lowData)) {
            WatchSetting lowSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                    .settingType(1)
                    .deviceId(deviceId)
                    .valueType(1)
                    .dataType(type).build()));

            if (lowSetting == null) {
                lowSetting = WatchSetting.builder()
                        .settingType(1)
                        .dataType(type)
                        .valueType(1)
                        .createTime(LocalDateTime.now())
                        .deviceId(deviceId).build();
            }

            lowSetting.setSettingValue(lowData);
            watchSettingService.saveOrUpdate(lowSetting);
        }


        // 高值报警
        if (StringUtil.isNotBlank(lowData)) {
            WatchSetting lowSetting = watchSettingService.getOne(new QueryWrapper<>(WatchSetting.builder()
                    .settingType(1)
                    .deviceId(deviceId)
                    .valueType(0)
                    .dataType(type).build()));

            if (lowSetting == null) {
                lowSetting = WatchSetting.builder()
                        .settingType(1)
                        .dataType(type)
                        .valueType(0)
                        .createTime(LocalDateTime.now())
                        .deviceId(deviceId).build();
            }

            lowSetting.setSettingValue(highData);
            watchSettingService.saveOrUpdate(lowSetting);
        }
        return CommonResultVo.SUCCESS();
    }

    private String sendWatch(String deviceId, Integer timeData, Integer type) {

        String data = "DW*" + deviceId;


        // 0血压 1血氧 2心率  3体温
        if (type == 0) {
            String len = toHex(timeData.toString().length() + 9, 4);
            data = "DW*" + deviceId + "*" + len + "*bldstart," + timeData;
        } else if (type == 1) {
            String len = toHex(timeData.toString().length() + 8, 4);
            data = "DW*" + deviceId + "*" + len + "*oxstart," + timeData;
        } else if (type == 2) {
            String len = toHex(timeData.toString().length() + 9, 4);
            data = "DW*" + deviceId + "*" + len + "*hrtstart," + timeData;
        } else if (type == 3) {
            String len = toHex(timeData.toString().length() + 8, 4);
            data = "DW*" + deviceId + "*" + len + "*wdstart," + timeData;
        }
        return data;
    }


    /**
     * 健康记录
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/healthRecords")
    public CommonResultVo<List<WatchHealth>> wearingRecords(String deviceId, String date, Integer type) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(date, "date 必传");
        ValidUtils.isNullThrow(type, "type 必传");

        QueryWrapper<WatchHealth> selectWarpper = new QueryWrapper<WatchHealth>();

        selectWarpper.between("create_time", date + " 00:00:00", date + " 23:59:59");
        selectWarpper.eq("device_id", deviceId);
        selectWarpper.eq("health_type", type);

        return CommonResultVo.SUCCESS(watchHealthService.list(selectWarpper));
    }


    /**
     * 获取电话本列表
     *
     * @param deviceId
     * @return
     */
    @RequestMapping("/getPhonebookList")
    public CommonResultVo<List<Phonebook>> getPhonebookList(String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        return CommonResultVo.SUCCESS(phonebookService.list(new QueryWrapper<>(Phonebook.builder()
                .deviceId(deviceId).build())));
    }

    /**
     * 保存电话本
     *
     * @param datas
     * @param deviceId
     * @return
     */
    @RequestMapping("/savePhonebook")
    public CommonResultVo<String> savePhonebook(String datas, String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");

        if (StringUtil.isBlank(datas)) {

            String data = "DW*" + deviceId + "*0003*PHL";

            watchPublishService.publish(WatchMsgDto.builder()
                    .deviceId(deviceId)
                    .data(data).build());

            phonebookService.remove(new QueryWrapper<>(Phonebook.builder().deviceId(deviceId).build()));
            return CommonResultVo.SUCCESS();
        }

        String[] dataList = datas.split(",");

        phonebookService.remove(new QueryWrapper<>(Phonebook.builder().deviceId(deviceId).build()));

        List<Phonebook> phonebookList = Lists.newArrayList();


        String values = "";

        for (String data : dataList
        ) {
            String nickName = data.split("-")[1];
            String mobile = data.split("-")[0];

            phonebookList.add(Phonebook.builder()
                    .deviceId(deviceId)
                    .nickname(nickName)
                    .mobile(mobile)
                    .createTime(LocalDateTime.now())
                    .build());
            values += mobile + "," + unicodeEncode(nickName) + ",";
        }

        if (StringUtil.isNotBlank(values)) {
            values = values.substring(0, values.length() - 1);
            String len = toHex(values.length() + 4, 4);

            String data = "DW*" + deviceId + "*" + len + "*PHL," + values;


            watchPublishService.publish(WatchMsgDto.builder()
                    .deviceId(deviceId)
                    .data(data).build());

            log.info("watch publish=========>deviceId={},data={}", deviceId, data);
        }

        phonebookService.saveBatch(phonebookList);
        return CommonResultVo.SUCCESS();
    }

    public String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + hexB;
        }
        return unicodeBytes;
    }


    /**
     * 解绑手表
     *
     * @param deviceId
     * @return
     */
    @RequestMapping("/deleteWatchDevice")
    public CommonResultVo<String> deleteWatchDevice(String deviceId) {

        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备不存在");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        // 解绑手表
        userDeviceService.removeById(watchDevice.getDeviceId());
        // 删除旧的联系人数据
        watchMobileService.remove(new QueryWrapper<>(WatchMobile.builder().deviceId(watchDevice.getDeviceId()).build()));
        // 删除电话本数据
        phonebookService.remove(new QueryWrapper<>(Phonebook.builder().deviceId(watchDevice.getDeviceId()).build()));
        // 删除报警记录
        watchSosService.remove(new QueryWrapper<>(WatchSos.builder()
                .deviceId(watchDevice.getDeviceId()).build()));
        // 删除设置信息
        watchSettingService.remove(new QueryWrapper<>(WatchSetting.builder()
                .deviceId(watchDevice.getDeviceId()).build()));
        // 删除手表健康数据
        watchHealthService.remove(new QueryWrapper<>(WatchHealth.builder()
                .deviceId(watchDevice.getDeviceId()).build()));

        // 删除电话本
        watchPublishService.publish(WatchMsgDto.builder()
                .deviceId(watchDevice.getDeviceId())
                .data("DW*" + deviceId + "*0003*PHL").build());

        // 删除SOS联系人
        watchPublishService.publish(WatchMsgDto.builder()
                .deviceId(watchDevice.getDeviceId())
                .data("DW*" + watchDevice.getDeviceId() + "*0003*SOS").build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 绑定手表设备
     *
     * @param watchSaveDto
     * @return
     */
    @RequestMapping("/bindWatchDevice")
    public CommonResultVo<String> bindWatchDevice(@RequestBody WatchSaveDto watchSaveDto) {

        UserDevice userDevice = userDeviceService.getById(watchSaveDto.getDeviceId());

        ValidUtils.noNullThrow(userDevice, "设备已被绑定");


        Product lockProduct = productService.getById("9999999998");

        userDevice = UserDevice.builder()
                .deviceId(watchSaveDto.getDeviceId())
                .userId(UserDto.getUser().getActualUserId())
                .productId(lockProduct.getProductId())
                .homeId(Long.valueOf(watchSaveDto.getHomeId()))
                .signalType(lockProduct.getProductType())
                .realProductType(lockProduct.getProductType())
                .topProductType(lockProduct.getProductType())
                .productType(lockProduct.getProductType())
                .status(true)
                .deviceName(lockProduct.getProductName())
                .customName(lockProduct.getProductName())
                .imagesUrl(lockProduct.getImagesUrl())
                .isShowScene(lockProduct.getIsShowScene())
                .thingModel(lockProduct.getThingModel()).build();

        //添加设备
        userDeviceService.save(userDevice);
        watchSettingService.saveBatch(createSetting(userDevice.getDeviceId()));

        UserDevice finalUserDevice = userDevice;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String data : settingData(finalUserDevice.getDeviceId())
                    ) {
                        watchPublishService.publish(WatchMsgDto.builder()
                                .deviceId(finalUserDevice.getDeviceId())
                                .data(data).build());
                        Thread.sleep(1200);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        return CommonResultVo.SUCCESS();
    }

    private List<WatchSetting> createSetting(String deviceId) {
        // 删除设置信息
        watchSettingService.remove(new QueryWrapper<>(WatchSetting.builder()
                .deviceId(deviceId).build()));

        List<WatchSetting> list = Lists.newArrayList();

        WatchSetting tempLow = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(3)
                .valueType(1)
                .createTime(LocalDateTime.now())
                .settingValue("36").build();

        WatchSetting tempHigh = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(3)
                .valueType(0)
                .createTime(LocalDateTime.now())
                .settingValue("37.3").build();


        WatchSetting heartLow = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(2)
                .valueType(1)
                .createTime(LocalDateTime.now())
                .settingValue("60").build();

        WatchSetting heartHigh = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(2)
                .valueType(0)
                .createTime(LocalDateTime.now())
                .settingValue("100").build();


        WatchSetting oxygenLow = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(1)
                .valueType(1)
                .createTime(LocalDateTime.now())
                .settingValue("95").build();

        WatchSetting oxygenHigh = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(1)
                .valueType(0)
                .createTime(LocalDateTime.now())
                .settingValue("100").build();


        WatchSetting pressureLow = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(0)
                .valueType(1)
                .createTime(LocalDateTime.now())
                .settingValue("90,139").build();

        WatchSetting pressureHigh = WatchSetting.builder()
                .deviceId(deviceId)
                .settingType(1)
                .dataType(0)
                .valueType(0)
                .createTime(LocalDateTime.now())
                .settingValue("60,89").build();

        list.add(pressureLow);
        list.add(pressureHigh);
        list.add(oxygenLow);
        list.add(oxygenHigh);
        list.add(tempLow);
        list.add(tempHigh);
        list.add(heartLow);
        list.add(heartHigh);
        return list;
    }

    private List<String> settingData(String deviceId) {

        List<String> list = Lists.newArrayList();

        // 设置血压上传频率
        list.add("DW*" + deviceId + "*000c*bldstart,300");
        // 设置血氧上传频率
        list.add("DW*" + deviceId + "*000b*oxstart,300");
        // 设置体温上传频率
        list.add("DW*" + deviceId + "*000b*wdstart,300");
        // 设置心率上传频率
        list.add("DW*" + deviceId + "*000c*hrtstart,300");
        // 立即定位
        list.add("DW*" + deviceId + "*0002*CR");
        // 数据上报间隔时间
        list.add("DW*" + deviceId + "*000a*UPLOAD,360");
        // 取下手表报警
        list.add("DW*" + deviceId + "*0008*REMOVE,1");
        return list;
    }


    /**
     * 找手表
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/findWatch")
    public CommonResultVo<String> findWatch(String deviceId) {
        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备不存在");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        watchPublishService.publish(WatchMsgDto.builder()
                .deviceId(watchDevice.getDeviceId())
                .data("DW*" + deviceId + "*0004*FIND").build());

        return CommonResultVo.SUCCESS();
    }


    /**
     * 设置手机号
     *
     * @param watchMobileDto
     * @return
     */
    @PostMapping("/settingWatchMobile")
    public CommonResultVo<String> settingWatchMobile(@RequestBody WatchMobileDto watchMobileDto) {
        UserDevice watchDevice = userDeviceService.getById(watchMobileDto.getDeviceId());
        ValidUtils.isNullThrow(watchDevice, "设备未绑定");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        // 删除旧的联系人数据
        watchMobileService.remove(new QueryWrapper<>(WatchMobile.builder().deviceId(watchDevice.getDeviceId()).build()));

        List<WatchMobile> saveList = Lists.newArrayList();

        for (String mobile : watchMobileDto.getMobiles().split(",")) {
            saveList.add(WatchMobile.builder().deviceId(watchDevice.getDeviceId()).wmMobile(mobile).createTime(LocalDateTime.now()).build());
        }

        // 保存db
        watchMobileService.saveBatch(saveList);

        String len = toHex(watchMobileDto.getMobiles().length() + 4, 4);

        log.info("settingMobilelength={},data={}", watchMobileDto.getMobiles().length(), watchMobileDto.getMobiles());

        String data = "DW*" + watchDevice.getDeviceId() + "*" + len + "*SOS," + watchMobileDto.getMobiles();


        watchPublishService.publish(WatchMsgDto.builder()
                .deviceId(watchDevice.getDeviceId())
                .data(data).build());
        log.info("watch publish=========>deviceId={},data={}", watchDevice.getDeviceId(), data);

        return CommonResultVo.SUCCESS();
    }


    public static String toHex(int serialNum, int length) {
        return String.format("%0" + length + "x", serialNum);
    }


    /**
     * 获取紧急联系人手机
     *
     * @return
     */
    @RequestMapping("/getWatchMobile")
    public CommonResultVo<List<WatchMobile>> getWatchMobile(String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId必填");

        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备未绑定");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        return CommonResultVo.SUCCESS(watchMobileService.list(new QueryWrapper<>(WatchMobile.builder().deviceId(watchDevice.getDeviceId()).build())));
    }


    /**
     * 手表详情
     *
     * @param deviceId
     * @return
     */
    @RequestMapping("/watchInfo")
    public CommonResultVo<WatchInfoVo> watchInfo(String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId必填");

        UserDevice watchDevice = userDeviceService.getById(deviceId);

        ValidUtils.isNullThrow(watchDevice, "设备未绑定");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        WatchInfoVo watchInfoVo = userDeviceService.findWatchInfo(deviceId);


        FenceSetting fenceSetting = fenceSettingService.getOne(new QueryWrapper<>(FenceSetting.builder()
                .userId(watchDevice.getUserId()).build()));

        if (fenceSetting != null) {
            watchInfoVo.setSettingLat(fenceSetting.getFenceLat());
            watchInfoVo.setSettingLng(fenceSetting.getFenceLng());
        }

        return CommonResultVo.SUCCESS(watchInfoVo);
    }
}

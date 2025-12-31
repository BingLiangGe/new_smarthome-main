package com.lj.iot.api.app.web.open;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.vo.ActivationVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.IAirControlWordService;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.biz.db.smart.service.ISpeechRecordService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ToPinYin;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 设备相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/open/device")
@CrossOrigin
public class DeviceController {

    private static final String APP_ID = "666666";
    private static final String APP_KEY = "8c337a8ca7de49f1ba43d3181181e92a";

    @Resource
    IDeviceService deviceService;
    @Resource
    IUserDeviceService userDeviceService;
    @Resource
    IAirControlWordService airControlWordService;
    @Resource
    private BizIrDeviceService bizIrDeviceService;
    @Resource
    private ISpeechRecordService speechRecordService;


    /**
     * 激活3326设备
     *
     * @param params
     * @return
     */
    @PostMapping("/activation3326")
    public CommonResultVo<ActivationVo> activation3326(@RequestBody Map<String, Object> params) throws IOException {

        ValidUtils.isNullThrow(params, "参数必传");
        ValidUtils.isNullThrow(params.get("appId"), "appId 参数必传");
        ValidUtils.isNullThrow(params.get("sign"), "sign 参数必传");
        ValidUtils.isNullThrow(params.get("time"), "time 参数必传");
        ValidUtils.isNullThrow(params.get("androidId"),"androidId 必填");

        if (!validation(params)) {
            return CommonResultVo.FAILURE_MSG("sign error!");
        }
        return deviceService.activation3326(params);
    }

    private boolean validation(Map<String, Object> params) {
        String sign = String.valueOf(params.get("sign"));

        params.remove("sign");

        String sign1 = getSign(params);
        params.remove("sign");

        log.info("sign1={},sign={}", sign1, sign);
        // 校验签名
        if (!StringUtils.equals(sign1, sign)) {// APPID查询的密钥进行签名 和 用户签名进行比对
            return false;
        }
        // 校验签名是否失效
        long thisTime = System.currentTimeMillis() - Long.valueOf((String) params.get("time"));
        log.info("thisTIme={}", thisTime);
        if (thisTime > 120000) {// 比对时间是否失效
            return false;
        }
        return true;
    }

    public static String getSign(Map<String, Object> params) {
        // 参数进行字典排序
        String sortStr = getFormatParams(params);
        // 将密钥key拼接在字典排序后的参数字符串中,得到待签名字符串。
        sortStr += "key=" + APP_KEY;
        // 使用md5算法加密待加密字符串并转为大写即为sign
        String sign = SecureUtil.md5(sortStr).toUpperCase();
        return sign;
    }

    /**
     * 参数字典排序
     *
     * @param params
     * @return
     */
    private static String getFormatParams(Map<String, Object> params) {
        List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(params.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
            public int compare(Map.Entry<String, Object> arg0, Map.Entry<String, Object> arg1) {
                return (arg0.getKey()).compareTo(arg1.getKey());
            }
        });
        String ret = "";
        for (Map.Entry<String, Object> entry : infoIds) {
            ret += entry.getKey();
            ret += "=";
            ret += entry.getValue();
            ret += "&";
        }
        return ret;
    }


    /**
     * 取消设置围栏
     *
     * @return
     */
    @RequestMapping("/removeSetting")
    public CommonResultVo<String> removeSetting(String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");

        UpdateWrapper updateWrapper = new UpdateWrapper();

        updateWrapper.eq("device_id", deviceId);
        updateWrapper.set("setting_lng", null);
        updateWrapper.set("setting_lat", null);
        updateWrapper.set("radius", 50);

        userDeviceService.update(updateWrapper);

        return CommonResultVo.SUCCESS();
    }


    /**
     * 设置手表地图
     *
     * @param deviceId
     * @param radius
     * @return
     */
    @RequestMapping("/settingWatchMap")
    public CommonResultVo<String> settingWatchMap(String deviceId, String lng, String lat, Integer radius) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(radius, "radius 必传");

        UserDevice watchDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(watchDevice, "设备不存在");

        // 非手表类型
        if (!"smart_watch".equals(watchDevice.getProductType())) {
            ValidUtils.isNullThrow(null, "该设备非智能手表");
        }

        watchDevice.setRadius(radius);
        watchDevice.setSettingLat(lat);
        watchDevice.setSettingLng(lng);

        return userDeviceService.updateById(watchDevice) ? CommonResultVo.SUCCESS() : CommonResultVo.FAILURE();
    }


    /**
     * 音频文件上传
     *
     * @param file
     * @return
     */
    @RequestMapping("/mp3Upload")
    public CommonResultVo<String> mp3Upload(String file) {
        log.info("file={}", file);
        return CommonResultVo.SUCCESS();
    }


    /**
     * 上传wifi账号密码
     *
     * @param wifiName
     * @param deviceId
     * @return
     */
    @RequestMapping("/uploadWifi")
    public CommonResultVo<String> uploadWifi(String wifiName, String deviceId) {
        UserDevice userDevice = userDeviceService.getById(deviceId);
        if (userDevice != null) {
            userDevice.setWifiName(wifiName);
            userDeviceService.updateById(userDevice);
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 获取 设备hash 主控用
     *
     * @param dto
     * @return
     */
    @RequestMapping("hash")
    public CommonResultVo<String> hash(@Valid DeviceIdDto dto) {
        return CommonResultVo.SUCCESS(deviceService.sha256(dto.getDeviceId()));
    }

    /**
     * 通过主控设备id获取 绑定的所有设备 主控用
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<Map>> hash(@RequestParam("masterDeviceId") String masterDeviceId) {
        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("master_device_id", masterDeviceId);
        wrapper.eq("is_show", true);
        List<UserDevice> list = userDeviceService.list(wrapper);
        List<Map> resultList = new ArrayList<>();
        for (UserDevice userDevice :
                list) {
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
            resultMap.put("thingModel", ThingModelMapList);
            if (customName.equals("kong1 diao4")) {
                customName = "kong1 tiao2";
            } else if (customName.equals("jiu3 ju3 deng1")) {
                customName = "jiu3 gui4 deng1";
            }
            resultMap.put("customName", customName);
            resultList.add(resultMap);
        }
        return CommonResultVo.SUCCESS(resultList);
    }

    @RequestMapping("getIrCode")
    public CommonResultVo<String> getIrCode(@RequestParam("masterId") String masterId, @RequestParam("keyWord") String keyWord) {
        log.info("getIrCode----->masterId:{},keyWork:{}", masterId, keyWord);
        String result = "获取成功";
        //判断热词是否存在
        AirControlWord one = airControlWordService.getOne(new QueryWrapper<>(AirControlWord.builder().pinyin(keyWord).build()));
        ValidUtils.isNullThrow(one, "控制命令不存在");
        //根据主控id来查用户
        QueryWrapper<UserDevice> wrapper = new QueryWrapper<>();
        wrapper.eq("master_device_id", masterId);
        wrapper.eq("is_show", true);
        wrapper.eq("device_name", one.getDeviceType());
        List<UserDevice> list = userDeviceService.list(wrapper); //主控下面的设备列表
        if (list.size() > 0) {
            //判断语音记录
//            speechRecordService.getOne(new QueryWrapper<>(SpeechRecord.builder().deviceId().build()));
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
                bizIrDeviceService.sendIrData(userDevice, thingModel, keyCode);
                //保存识别记录
                speechRecordService.save(SpeechRecord.builder()
                        .deviceId(userDevice.getDeviceId())
                        .userId(userDevice.getUserId())
                        .homeId(userDevice.getHomeId())
                        .intentName("离线语音控制")
                        .text(one.getCnName())
                        .build());
            }

        }

        return CommonResultVo.SUCCESS(result);
    }

}

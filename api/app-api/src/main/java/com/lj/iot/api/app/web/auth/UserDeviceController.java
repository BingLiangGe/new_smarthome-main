package com.lj.iot.api.app.web.auth;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sms.service.ISmsService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用户设备相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/user_device")
public class UserDeviceController {
    @Resource
    IUserDeviceService userDeviceService;

    @Resource
    BizUserDeviceService bizUserDeviceService;

    @Resource
    BizIrDeviceService bizIrDeviceService;

    @Resource
    BizRfDeviceService bizRfDeviceService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IDeviceGroupService deviceGroupService;

    @Autowired
    private IDeviceService deviceService;

    @Resource(name = "SmsServiceImpl")
    private ISmsService smsService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    @Resource
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IDeviceRecordService deviceRecordService;


    /**
     * 清除子设备
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/cleanSubDevice")
    public CommonResultVo<String> cleanSubDevice(String masterDeviceId) {
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId 必传");

        // 查询设备数据是否存在
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(masterDeviceId)
                .build()));
        ValidUtils.isNullThrow(userDevice, "设备数据不存在");

        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .masterDeviceId(masterDeviceId)
                .productType("room_lock").build()));

        if (!list.isEmpty()) {

            List<DeviceRecord> records = Lists.newArrayList();
            for (UserDevice lockDevice : list
            ) {
                records.add(DeviceRecord.builder()
                        .createTime(LocalDateTime.now())
                        .productId(lockDevice.getProductId())
                        .deviceId(lockDevice.getDeviceId())
                        .userId(UserDto.getUser().getActualUserId()).build());

                userDeviceService.removeById(lockDevice.getDeviceId());
            }
            deviceRecordService.saveBatch(records);
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 校验设备状态
     *
     * @param deviceId 设备id
     * @param status   状态 0关 1开
     * @return
     */
    @RequestMapping("/chechDeviceStatus")
    public CommonResultVo<String> chechDeviceStatus(String deviceId, Integer status) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");
        ValidUtils.isNullThrow(status, "status 必传");

        // 查询设备数据是否存在
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(deviceId)
                .build()));

        ValidUtils.isNullThrow(userDevice, "设备数据不存在");
        ValidUtils.isFalseThrow("IR".equals(userDevice.getSignalType()), "只支持红外设备更改");

        userDevice.getThingModel().getProperties().get(0).setValue(status);

        userDeviceService.updateById(userDevice);

        bizWsPublishService.publishEditMemberByHomeId(
                RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_POST,
                userDevice.getHomeId(),
                userDeviceService.getById(userDevice.getDeviceId()));

        return CommonResultVo.SUCCESS();
    }


    /**
     * 射频窗帘调转
     *
     * @return
     */
    @RequestMapping("/curtainRfCodeReset")
    public CommonResultVo<String> curtainRfCodeReset(String deviceId) {

        //查询主控是否存在，不存在则绑定，同时发送token给主控
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(deviceId)
                .build()));

        ValidUtils.isNullThrow(userDevice, "设备数据不存在");

        UserDeviceRfKey openRf = userDeviceRfKeyService.findByDeviceIdAndCode(deviceId, "open");
        UserDeviceRfKey closeRf = userDeviceRfKeyService.findByDeviceIdAndCode(deviceId, "close");

        ValidUtils.isNullThrow(openRf, "'打开'射频码不存在");
        ValidUtils.isNullThrow(closeRf, "'关闭'射频码不存在");

        String openCode = openRf.getCodeData();
        String closeCode = closeRf.getCodeData();

        openRf.setCodeData(closeCode);
        closeRf.setCodeData(openCode);

        userDeviceRfKeyService.updateById(openRf);
        userDeviceRfKeyService.updateById(closeRf);

        return CommonResultVo.SUCCESS();
    }


    /**
     * 编辑设备密码
     *
     * @param doorPwdDto
     * @return
     */
    @PostMapping("/editDevicePwd")
    public CommonResultVo editDevicePwd(@RequestBody @Valid DoorPwdDto doorPwdDto) {
        Device device = deviceService.getById(doorPwdDto.getDeviceId());

        ValidUtils.isNullThrow(device, "设备不存在");
        ValidUtils.isNullThrow(doorPwdDto.getCode(), "验证码必填");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(doorPwdDto.getDeviceId()).userId(UserDto.getUser().getActualUserId()).build()));

        ValidUtils.isNullThrow(userDevice, "无权限");

        String key = "app" + RedisConstant.code_check + UserDto.getUser().getAccount();
        String redisCode = cacheService.get(key);
        ValidUtils.isFalseThrow(doorPwdDto.getCode().equals(redisCode), "验证码不正确，请查证后再试");

        userDevice.setDoorPwd(doorPwdDto.getDoorPwd());

        userDeviceService.updateById(userDevice);
        return CommonResultVo.SUCCESS();
    }


    /**
     * 获取验证码
     *
     * @return
     */
    @PostMapping("/sendSms")
    public CommonResultVo<String> sendSms() {
        sendSms(UserDto.getUser().getAccount());
        return CommonResultVo.SUCCESS();
    }

    private void sendSms(String account) {
        String key = "app" + RedisConstant.code_check + account;
        String code = smsService.sendVerificationCode(account);
        cacheService.addSeconds(key, code, 60 * 5 * 1000L);
    }


    /**
     * 验证用户是否设置门锁密码
     *
     * @return
     */
    @GetMapping("/checkSettingDoorPwd")
    public CommonResultVo checkSettingDoorPwd(String deviceId) {

        Device device = deviceService.getById(deviceId);

        ValidUtils.isNullThrow(device, "设备不存在");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(device.getId()).userId(UserDto.getUser().getActualUserId()).build()));

        ValidUtils.isNullThrow(userDevice, "无权限");

        JSONObject respJson = new JSONObject();

        if (userDevice.getDoorPwd() == null) {
            respJson.put("is_setting", false);
            return CommonResultVo.SUCCESS(respJson);
        }
        respJson.put("is_setting", true);
        return CommonResultVo.SUCCESS(respJson);
    }

    /**
     * 验证门密码
     *
     * @return
     */
    @PostMapping("/checkDoorPwd")
    public CommonResultVo checkDoorPwd(@RequestBody @Valid DoorPwdDto doorPwdDto) {

        Device device = deviceService.getById(doorPwdDto.getDeviceId());

        ValidUtils.isNullThrow(device, "设备不存在");

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(doorPwdDto.getDeviceId()).build()));

        ValidUtils.isNullThrow(userDevice, "无权限");

        if (!doorPwdDto.getDoorPwd().equals(userDevice.getDoorPwd())) {
            throw CommonException.FAILURE("密码错误");
        }
        return CommonResultVo.SUCCESS();
    }


    /**
     * 导入之前设备
     *
     * @param file
     * @return
     */
    @RequestMapping("/importBeforDevice")
    public CommonResultVo<String> importBeforDevice(@RequestPart("file") MultipartFile file) throws IOException {

        List<DeviceExcelImportDto> list = EasyExcel.read(file.getInputStream())
                .head(DeviceExcelImportDto.class)
                .sheet()
                .doReadSync();

        for (DeviceExcelImportDto deviceExcelImportDto : list
        ) {
            UserDevice userDevice = userDeviceService.getById(deviceExcelImportDto.getDeviceId());

            if (userDevice == null) {
                log.info("设备不存在,deviceId={}", deviceExcelImportDto.getDeviceId());
                continue;
            }
            log.info("设备号={},硬件版本号={},软件版本号={}", userDevice.getDeviceId(), userDevice.getHardWareVersion(), userDevice.getSoftWareVersion());
        }

        log.info("listSize:{}", list.size());
        return CommonResultVo.SUCCESS();
    }



    /* todo 导入设备emqx账号信息 *//**
     * 导入漏掉的设备信息至emqx
     *
     * @param file
     * @return
     *//*
    @RequestMapping("/importDevice")
    public CommonResultVo<String> importDevice(@RequestPart("file") MultipartFile file) {

        try {
            *//*List<DeviceExcelImportDto> list = EasyExcel.read(file.getInputStream())
                    .head(DeviceExcelImportDto.class)
                    .sheet()
                    .doReadSync();

            List<Device> deviceList = new ArrayList<>();

            list.forEach(dto -> {
                Device device = deviceService.getById(dto.getDeviceId());

                if (device != null) {
                    deviceList.add(device);
                }else{
                    System.out.println("不存在设备："+dto.getDeviceId());
                }
            });

            List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                            .user_id(device.getId())
                            .password_hash(device.getCCCFDF())
                            .build())
                    .collect(Collectors.toList());
*//*
            //创建json文件
            String path = "C:\\Users\\A\\Desktop\\inemqx.json";
            //createTxt(JSON.toJSONString(deviceJsonVoList), path);
            //上传给emqx
            import_users(path);

            //System.out.println(deviceList.size());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CommonResultVo.SUCCESS();
    }

    *//* 文件上传到emqx
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     *//*
    public String import_users(String path) throws IOException {
        //账号密码
        String credential = Credentials.basic("admin", "public");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        okhttp3.RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("filename", path,
                        okhttp3.RequestBody.create(MediaType.parse("application/octet-stream"),
                                new File(path)))
                .build();
        Request request = new Request.Builder()
                .url("http://47.107.86.36:18083/api/v5/authentication/password_based%3Abuilt_in_database/import_users")
                .method("POST", body)
                .header("Authorization", credential)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);
        return response.body().string();
    }

    public void createTxt(String text, String path) throws IOException {
        try {
            log.info("##############################文件创建开始################################");
            File destFile = new File(path);
            destFile.createNewFile();
            Writer write = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
            write.write(text);
            write.flush();
            write.close();
            log.info("##############################文件创建成功################################");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /**
     * 查询家下面的主控设备列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("master_list")
    public CommonResultVo<List<UserDevice>> masterList(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(userDeviceService.masterList(dto));
    }

    /**
     * 查询家下首页显示设备列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("page")
    public CommonResultVo<IPage<UserDeviceVo>> page(@Valid HomeIdRoomIdPageDto dto) {

        dto.setUserId(UserDto.getUser().getActualUserId());
        IPage<UserDeviceVo> userDeviceVoIPage = userDeviceService.customShowPage(dto);
        List<UserDeviceVo> records = userDeviceVoIPage.getRecords();
        for (int i = 0; i < records.size(); i++) {
            if (!records.get(i).getSignalType().equals(SignalEnum.MESH.getCode()) && !records.get(i).getProductType().equals("smart_watch") && !"socket".equals(records.get(i).getProductType()) && !"light_ct".equals(records.get(i).getProductType())) {
                String masterDeviceId = records.get(i).getMasterDeviceId();
                UserDevice byId = userDeviceService.getById(masterDeviceId);
                records.get(i).setStatus(byId.getStatus());
            }

            if ("13093133".equals(records.get(i).getProductId())) {
                List<SceneThingModelVo> sceneThingModelVos = userDeviceService.getSceneThingModel(records.get(i).getDeviceId());
                List<ThingModelProperty> thingModels = records.get(i).getThingModel().getProperties();


                for (ThingModelProperty property : thingModels
                ) {
                    for (SceneThingModelVo sceneThingModelVo : sceneThingModelVos
                    ) {
                        if (sceneThingModelVo.getIdentifier().equals(property.getIdentifier())){
                            property.setSceneId(sceneThingModelVo.getSceneId());
                        }
                    }
                }
            }
        }
        return CommonResultVo.SUCCESS(userDeviceVoIPage);
    }


    /**
     * 查询家下首页显示设备列表_3326
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("page3326")
    public CommonResultVo<IPage<UserDeviceVo>> page3326(@Valid HomeIdRoomIdPageDto dto) {

        dto.setUserId(UserDto.getUser().getActualUserId());
        IPage<UserDeviceVo> userDeviceVoIPage = userDeviceService.customShowPage3326(dto);
        List<UserDeviceVo> records = userDeviceVoIPage.getRecords();
        for (int i = 0; i < records.size(); i++) {
            if (!records.get(i).getSignalType().equals(SignalEnum.MESH.getCode()) && !records.get(i).getProductType().equals("smart_watch") && !"socket".equals(records.get(i).getProductType()) && !"light_ct".equals(records.get(i).getProductType())) {
                String masterDeviceId = records.get(i).getMasterDeviceId();
                UserDevice byId = userDeviceService.getById(masterDeviceId);
                records.get(i).setStatus(byId.getStatus());
            }
        }
        return CommonResultVo.SUCCESS(userDeviceVoIPage);
    }


    /**
     * 查询设备详情数据
     *
     * @param dto
     * @return
     */
    //@HomeAuth(value = "deviceId", type = HomeAuth.PermType.ALL)
    @RequestMapping("info")
    public CommonResultVo<UserDevice> info(@Valid DeviceIdDto dto) {
        log.info("UserDeviceController.info{}", JSON.toJSONString(dto));
        return CommonResultVo.SUCCESS(userDeviceService.queryInfo(dto));
    }

    /**
     * 查询多联多控可绑定的设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("show_bind_list")
    public CommonResultVo<List<UserDeviceBindVo>> showBindList(@Valid UserDeviceBindDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.showBindList(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 安卓查询多联多控可绑定的设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("androd_show_bind_list")
    public CommonResultVo<List<UserDeviceBindVo>> androidshowBindList(@Valid UserDeviceBindDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.androidshowBindList(dto));
    }

    @PostMapping("/switchGroups")
    public CommonResultVo<String> switchGroups(@RequestBody @Valid SwitchGroupsDto dto) {
        ValidUtils.isNullThrow(dto, "入参必填");
        bizUserDeviceService.switchGroup(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 多联多控设备绑定
     *
     * @param dto
     * @return
     */
    @PostMapping("bind_device")
    public CommonResultVo<List<String>> bindDevice(@RequestBody @Valid DeviceBindDto dto) {
        List<String> deviceIds = dto.getDeviceIds();
        if (deviceIds.size() > 1) {
            return CommonResultVo.SUCCESS(bizUserDeviceService.bindDevice(dto, UserDto.getUser().getActualUserId()));
        } else {
            //绑定的产品只有一个数组的时候，删掉数据
            if (deviceIds.size() != 0) {
                String s = deviceIds.get(0);
                UserDevice byId = userDeviceService.getById(s);
                DelDeviceBindDto delDeviceBindDto = new DelDeviceBindDto();
                delDeviceBindDto.setGroupId(byId.getGroupId());
                bizUserDeviceService.delBindDevice(delDeviceBindDto, UserDto.getUser().getActualUserId());
            }

        }

        return CommonResultVo.SUCCESS(bizUserDeviceService.bindDevice(dto, UserDto.getUser().getActualUserId()));
    }


    /**
     * 解除多联多控设备绑定
     *
     * @param dto
     * @return
     */
    @PostMapping("del_bind_device")
    public CommonResultVo delBindDevice(@RequestBody @Valid DelDeviceBindDto dto) {
        bizUserDeviceService.delBindDevice(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    @Value("${mqtt.client.host}")
    private String host;

    /**
     * 主控设备添加
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("add_master")
    public CommonResultVo<String> addMaster(@RequestBody @Valid MasterDeviceDto dto) {

        // 校验环境
        if (dto.getIp() != null) {

            if (!host.equals(dto.getIp())) {
                ValidUtils.isNullThrow(null, "环境不一致,固件版本:" + dto.getIp() + ",APP版本:" + host + ",请联系管理员");
            }
        }

        //查询主控是否存在，不存在则绑定，同时发送token给主控
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId())
//                .homeId(dto.getHomeId())
                .build()));

        UserAccount beforUser = null;

        if (userDevice != null) {
            beforUser = userAccountService.getById(userDevice.getUserId());
            ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser.getMobile() + ",deviceId:" + userDevice.getDeviceId());
        }
        if (userDevice == null) {
            userDevice = bizUserDeviceService.addMasterDevice(dto, UserDto.getUser().getUId());
        }


        UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder().mobile(userDevice.getDeviceId()).build()));

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder().platform(PlatFormEnum.APP.getCode()).uId(user.getId()).account(user.getMobile()).actualUserId(user.getActualUserId()).build());

        //把token mqtt推送到硬件设备
        mqttPushService.pushLoginToken(userDevice, LoginVo.<UserAccount>builder().account(user.getMobile()).userInfo(user).token(token).params(userDevice.getHomeId()).build());


       /* todo 取消自动升级 //主控设备下的所有离线设备上线（排除mesh类型及虚设备）
        UserDevice masterDevice = userDeviceService.getById(userDevice.getDeviceId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("进入系统推送sleep30秒===>");
                    Thread.sleep(1000 * 30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_BIND_SUCCESS, masterDevice.getProductId(), masterDevice.getDeviceId());

                //版本不相同请求更新
                MqttOtaDto mqttOtaDto = MqttOtaDto.builder().details("HB").build();
                MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));
            }
        }).start();*/

        return CommonResultVo.SUCCESS(token);
    }

    /**
     * 新增设备
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "masterDeviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("add")
    public CommonResultVo<UserDevice> add(@RequestBody @Valid UserDeviceAddDto dto) {
        log.info("UserDeviceController.add{}", JSON.toJSONString(dto));
        return CommonResultVo.SUCCESS(bizUserDeviceService.add(dto, UserDto.getUser().getActualUserId()));
    }


    /**
     * 新增设备
     *
     * @param dto
     * @return
     */
    @PostMapping("addDeviceTest")
    public CommonResultVo<UserDevice> addDeviceTest(@RequestBody @Valid UserDeviceAddDto dto) {
        log.info("addDeviceTest.add{}", JSON.toJSONString(dto));
        return CommonResultVo.SUCCESS(bizUserDeviceService.addDeviceTest(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 主控设备重启
     *
     * @param deviceIdDto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("restart_master")
    public CommonResultVo restartMaster(@RequestBody @Valid DeviceIdDto deviceIdDto) {
        bizUserDeviceService.restartMasterDevice(deviceIdDto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 重置mesh
     *
     * @param deviceIdDto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("reset_mesh")
    public CommonResultVo resetMesh(@RequestBody @Valid DeviceIdDto deviceIdDto) {
        bizUserDeviceService.resetMesh(deviceIdDto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询可绑定蓝牙设备列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "masterDeviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("search_new_mesh")
    public CommonResultVo<String> searchNewMesh(SearchMeshDeviceDto dto) {
        bizUserDeviceService.searchNewMesh(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 删除设备
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid DeviceIdDto dto) {

        UserDevice userDevice = bizUserDeviceService.delete(dto, UserDto.getUser().getActualUserId());

        ValidUtils.isNullThrow(userDevice, "设备不存在");

        // 主控删除升级队列
        if ("MASTER".equals(userDevice.getSignalType())) {
            upgradeRecordService.remove(new QueryWrapper<>(UpgradeRecord.builder().deviceId(userDevice.getDeviceId()).build()));
        }

        //主控设备或者主控对应的虚设备 且这个账号是对应主控登录账号。不允许删除
        if (userDevice.getMasterDeviceId().equals(userDevice.getPhysicalDeviceId()) && UserDto.getUser().getAccount().equals(userDevice.getMasterDeviceId())) {
            return CommonResultVo.FAILURE_MSG("本身自带的设备不允许删除");
        }

        //主控设备或者主控对应的虚设备 去掉对应的账号，去掉登录token
        if (userDevice.getMasterDeviceId().equals(userDevice.getPhysicalDeviceId())) {
            UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder().mobile(userDevice.getMasterDeviceId()).build()));
            if (user != null) {
                bizUserAccountService.cancellation(user.getId());
                LoginUtils.logout(UserDto.builder().platform(PlatFormEnum.APP.getCode()).uId(user.getId()).account(user.getMobile()).actualUserId(user.getActualUserId()).build());
            }
        }

        //判断删除组后长度是否等于1，如果是1，整个组都要接触
        List<DeviceGroup> list1 = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(userDevice.getGroupId()).build()));
        if (list1.size() <= 2 & list1.size() > 0) {
            DelDeviceBindDto delDeviceBindDto = new DelDeviceBindDto();
            delDeviceBindDto.setGroupId(userDevice.getGroupId());
            bizUserDeviceService.delBindDevice(delDeviceBindDto, UserDto.getUser().getUId());
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑设备
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("edit")
    public CommonResultVo<String> edit(@RequestBody @Valid UserDeviceEditDto dto) {
        bizUserDeviceService.edit(Collections.singletonList(dto), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑设备
     *
     * @param dto
     * @return
     */
    @PostMapping("edit_batch")
    public CommonResultVo<String> editBatch(@RequestBody @Valid UserDeviceEditBatchDto dto) {
        bizUserDeviceService.edit(dto.getList(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 测试发送红外码
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "masterDeviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("test_ir_data")
    public CommonResultVo<String> testIrData(@RequestBody TestIrDataDto dto) {
        bizIrDeviceService.testIrData(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 发送数据
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.ALL)
    @PostMapping("send_data")
    public CommonResultVo<String> sendData(@RequestBody @Valid SendDataDto dto) {
        bizUserDeviceService.sendData(dto, OperationEnum.APP_C);
        return CommonResultVo.SUCCESS();
    }

    /**
     * 学习射频码
     *
     * @param studyRfDataDto
     * @return
     */
    //@HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("study_rf_data")
    public CommonResultVo<String> studyRfData(@RequestBody StudyRfDataDto studyRfDataDto) {
        bizRfDeviceService.learnRfData(studyRfDataDto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 学习射频码2
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.EDIT)
    @PostMapping("study_rf_data2")
    public CommonResultVo<String> studyRfData2(@RequestBody StudyRfData2Dto dto) {
        bizRfDeviceService.learnRfData(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 场景添加列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @RequestMapping("scene_add_device_list")
    public CommonResultVo<List<SceneAddDeviceListVo>> sceneAddDeviceList(SceneAddDeviceDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.sceneAddDeviceList(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 获取控制器设备
     *
     * @param controlDeviceto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("list_control_device")
    public CommonResultVo<List<UserDevice>> listControlDevice(@RequestBody @Valid ControlDeviceto controlDeviceto) {
        return CommonResultVo.SUCCESS(userDeviceService.listControlDevice(controlDeviceto));
    }

    /**
     * 切换组
     *
     * @return
     */
    @PostMapping("device_switch")
    public CommonResultVo<String> device_switch(@RequestBody DeviceBindSwitchDto deviceBindSwitchDto) {
        if (deviceBindSwitchDto.getGroupId().equals("")) {
            //如果没有传group_id 代表解绑了，不需要
            UserDevice one = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(deviceBindSwitchDto.getDeviceId()).build()));
            ValidUtils.isNullThrow(one, "DeviceId数据不存在");
            DeviceBindDto dto = new DeviceBindDto();
            dto.setDeviceId(deviceBindSwitchDto.getDeviceId());
            dto.setAction(false);


            //  查询当前组的list
            List<DeviceGroup> nowList = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(one.getGroupId()).userId(UserDto.getUser().getUId()).build()));
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < nowList.size(); i++) {
                String deviceId = nowList.get(i).getDeviceId();
                if (!one.getDeviceId().equals(deviceId)) {
                    //把本身剔除，然后让设备
                    arrayList.add(nowList.get(i).getDeviceId());
                }
            }
            dto.setDeviceIds(arrayList);
            userDeviceService.updateById(UserDevice.builder().deviceId(deviceBindSwitchDto.getDeviceId()).groupId(one.getGroupId()).build());
            //让设备重新绑定一次
            bizUserDeviceService.bindDevice(dto, UserDto.getUser().getUId());
            return CommonResultVo.SUCCESS();


        }


        //查看device_group 组是否自身，如果没有自身就新增
        List<DeviceGroup> list = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().deviceId(deviceBindSwitchDto.getDeviceId()).groupId(deviceBindSwitchDto.getGroupId()).userId(UserDto.getUser().getUId()).build()));
        DeviceBindDto dto = new DeviceBindDto();
        dto.setDeviceId(deviceBindSwitchDto.getDeviceId());
        dto.setAction(false);
        if (list.size() == 0) {
            //  查询当前组的list
            List<DeviceGroup> nowList = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(deviceBindSwitchDto.getGroupId()).userId(UserDto.getUser().getUId()).build()));
            //查询当前设备所在的组，并把数据删掉
            List<DeviceGroup> nowDeviceList = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().deviceId(deviceBindSwitchDto.getDeviceId()).userId(UserDto.getUser().getUId()).build()));

            deviceGroupService.removeBatchByIds(nowDeviceList);

            if (nowList.size() > 0) {
                ArrayList<String> arrayList = new ArrayList<>();
                //先加自身
                arrayList.add(deviceBindSwitchDto.getDeviceId());
                for (int i = 0; i < nowList.size(); i++) {
                    arrayList.add(nowList.get(i).getDeviceId());
                }
                dto.setDeviceIds(arrayList);
            }
            userDeviceService.updateById(UserDevice.builder().deviceId(deviceBindSwitchDto.getDeviceId()).groupId(deviceBindSwitchDto.getGroupId()).build());
            //让设备重新绑定一次
            bizUserDeviceService.bindDevice(dto, UserDto.getUser().getUId());
            return CommonResultVo.SUCCESS();
        }
        userDeviceService.updateById(UserDevice.builder().deviceId(deviceBindSwitchDto.getDeviceId()).groupId(deviceBindSwitchDto.getGroupId()).build());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 请求扫描
     *
     * @return
     */
    @PostMapping("scan_device")
    public CommonResultVo<String> scanDevice(@RequestBody UserDeviceAddDto userDeviceAddDto) {
        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(userDeviceAddDto.getMasterDeviceId()).build()));
        mqttPushService.scanDevice(list.get(0).getMasterProductId(), userDeviceAddDto.getProductId(), userDeviceAddDto.getMasterDeviceId());
        return CommonResultVo.SUCCESS();
    }

}

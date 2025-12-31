package com.lj.iot.api.hotel.web.auth;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.MasterDeviceDto;
import com.lj.iot.biz.base.vo.SceneAddDeviceListVo;
import com.lj.iot.biz.base.vo.UserDeviceBindVo;
import com.lj.iot.biz.base.vo.UserDeviceVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.biz.service.BizRfDeviceService;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IHotelFloorHomeService hotelFloorHomeService;


    @Autowired
    private IDeviceGroupService groupService;

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private IProductUpgradeService productUpgradeService;


    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IDeviceRecordService deviceRecordService;

    @Autowired
    private IApiConfigService apiConfigService;

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

            List<DeviceRecord> records= Lists.newArrayList();
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
     * 绑定主控上报
     *
     * @return
     */
    @RequestMapping("/appBindMasterDevice")
    public CommonResultVo<String> appBindMasterDevice(String masterDeviceId,String homeId) {
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");
        ValidUtils.isNullThrow(homeId, "homeId必传");

        String key = "bind_master_device_" + masterDeviceId;
        cacheService.addSeconds(key, UserDto.getUser().getUId()+","+homeId, 60 * 1000L);

        log.info("APP进入绑定主控上报,deviceId={},userId={}", masterDeviceId, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 检查主控版本
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/checkMasterDeviceVersion")
    public CommonResultVo<String> checkMasterDeviceVersion(String masterDeviceId) {

        log.info("checkMasterDeviceVersion======deviceId={}", masterDeviceId);
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(userDevice, "主控不存在,或未绑定");

        String softWareVersion = userDevice.getSoftWareVersion();
        String headWareVersion = userDevice.getHardWareVersion();

        log.info("softWareVersion={},headWareVersion={},productId={}", softWareVersion, headWareVersion, userDevice.getProductId());

        // 存在软件版本号+ 硬件版本号
        if (softWareVersion != null && headWareVersion != null) {

            ProductUpgrade productUpgrade = productUpgradeService.findNewUpgradeByProduct(userDevice.getProductId(), headWareVersion, softWareVersion);

            if (productUpgrade != null) {
                return CommonResultVo.SUCCESS();
            }
        }
        return CommonResultVo.FAILURE_MSG("已是最新版本");
    }


    /**
     * 主控重启
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/masterDeviceReset")
    public CommonResultVo<String> masterDeviceReset(String masterDeviceId) {
        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(userDevice, "主控不存在,或未绑定");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("productId", userDevice.getProductId());
        jsonObject.put("deviceId", userDevice.getDeviceId());

        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getMasterDeviceId());
        MqttParamDto paramDto = MqttParamDto.builder()
                .id(IdUtil.simpleUUID())
                .time(DateUtil.current())
                .data(jsonObject)
                .build();
        MQTT.publish(topic, JSON.toJSONString(paramDto));
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));
        return CommonResultVo.SUCCESS();
    }


    /**
     * 主控升级
     *
     * @param masterDeviceId
     * @return
     */
    @RequestMapping("/masterDeviceUpgradation")
    public CommonResultVo<String> masterDeviceUpgradation(String masterDeviceId) {

        ValidUtils.isNullThrow(masterDeviceId, "masterDeviceId必传");

        UserDevice userDevice = userDeviceService.getById(masterDeviceId);

        ValidUtils.isNullThrow(userDevice, "主控不存在,或未绑定");

        String softWareVersion = userDevice.getSoftWareVersion();
        String headWareVersion = userDevice.getHardWareVersion();

        // 存在软件版本号+ 硬件版本号
        if (softWareVersion != null && headWareVersion != null) {

            ProductUpgrade productUpgrade = productUpgradeService.findNewUpgradeByProduct(userDevice.getProductId(), headWareVersion, softWareVersion);

            log.info("进入升级前校验deviceId={},sof={},hard={},升级={}", masterDeviceId, softWareVersion, headWareVersion, productUpgrade);
            if (productUpgrade != null) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("productId", userDevice.getProductId());
                jsonObject.put("deviceId", userDevice.getDeviceId());

                // 重启设备
                String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_RESTART, userDevice.getProductId(), userDevice.getPhysicalDeviceId());
                MqttParamDto paramDto = MqttParamDto.builder()
                        .id(IdUtil.simpleUUID())
                        .time(DateUtil.current())
                        .data(jsonObject)
                        .build();

                MQTT.publish(topic, JSON.toJSONString(paramDto));
                log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(paramDto));


                upgradeRecordService.save(UpgradeRecord.builder().
                        deviceId(masterDeviceId)
                        .createTime(LocalDateTime.now())
                        .filePath(productUpgrade.getVersionUrl())
                        .softWareVersion(productUpgrade.getNewVersion())
                        .hardWareVersion(productUpgrade.getHardWareVersion())
                        .isSuccess(0).build());

                return CommonResultVo.SUCCESS();
            }
        }
        return CommonResultVo.FAILURE_MSG("无可升级的固件包");
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
     * 查询家下面的主控设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("master_list")
    @CustomPermissions("user_device:master_list")
    public CommonResultVo<List<UserDevice>> masterList(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(userDeviceService.masterHotelList(dto));
    }

    /**
     * 查询家下首页显示设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("page")
    @CustomPermissions("user_device:page")
    public CommonResultVo<IPage<UserDeviceVo>> page(@Valid HomeIdRoomIdPageDto dto) {
        IPage<UserDeviceVo> userDeviceVoIPage = userDeviceService.customShowPage(dto);
        HomeStatus homeStatus = new HomeStatus();
        if (userDeviceVoIPage.getSize() > 0) {
            long onLine = userDeviceService.findBySumStatus(dto.getHomeId());
            long total = userDeviceVoIPage.getTotal();
            long offLine = total - onLine;
            homeStatus = HomeStatus.builder()
                    .offLine(offLine)
                    .onLine(onLine)
                    .temperature("22")
                    .build();
        }


        return CommonResultVo.HOTELSTATUSSUCCESS(userDeviceVoIPage, homeStatus);
    }


    /**
     * 查询多联多控可绑定的设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("show_bind_list")
    @CustomPermissions("user_device:show_bind_list")
    public CommonResultVo<List<UserDeviceBindVo>> showBindList(@Valid UserDeviceBindDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.showBindList(dto, UserDto.getUser().getActualUserId()));
    }


    @RequestMapping("showBindGroupList")
    public CommonResultVo<List<DeviceGroup>> showBindGroupList(String groupId) {
        List<DeviceGroup> list = groupService.list(new QueryWrapper<>(DeviceGroup.builder()
                .groupId(groupId).build()));

        list.forEach(group -> {
            UserDevice userDevice = userDeviceService.getById(group.getDeviceId());

            if (userDevice != null) {
                group.setCustomName(userDevice.getCustomName());
            }
        });

        return CommonResultVo.SUCCESS(list);
    }


    /**
     * 多联多控设备绑定
     *
     * @param dto
     * @return
     */
    @PostMapping("bind_device")
    @CustomPermissions("user_device:bind_device")
    public CommonResultVo<List<UserDeviceBindVo>> delBindDevice(@RequestBody @Valid DeviceBindDto dto) {
        bizUserDeviceService.bindDevice(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 一键解除多联多控设备绑定
     *
     * @param dto
     * @return
     */
    @PostMapping("del_bind_device")
    @CustomPermissions("user_device:del_bind_device")
    public CommonResultVo<List<UserDeviceBindVo>> delBindDevice(@RequestBody @Valid DelDeviceBindDto dto) {
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
    @PostMapping("add_master")
    @CustomPermissions("user_device:add_master")
    public CommonResultVo<String> addMaster(@RequestBody @Valid MasterDeviceDto dto) {

        //校验房间和酒店的关系
        HotelFloorHome hotelFloorHome = hotelFloorHomeService.getOne(new QueryWrapper<>(HotelFloorHome.builder()
                .hotelId(UserDto.getUser().getHotelId())
                .homeId(dto.getHomeId())
                .build()));
        ValidUtils.isNullThrow(hotelFloorHome, "家庭数据不存在");

        //查询主控是否存在，不存在则绑定，同时发送token给主控
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice
                .builder()
                .deviceId(dto.getDeviceId())
//                .homeId(dto.getHomeId())
                .build()));


        if (userDevice != null) {
            HotelUserAccount beforUser = hotelUserAccountService.getById(userDevice.getUserId());

            if (beforUser != null) {
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            }

            UserAccount userAccount = userAccountService.getById(userDevice.getUserId());
            if (userAccount != null) {
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + userAccount.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            }

        }

        if (userDevice == null) {
            bizUserDeviceService.hotelAddMasterDevice(dto, UserDto.getUser().getActualUserId());
        }
        userDevice = userDeviceService.masterStatus(dto.getDeviceId(), UserDto.getUser().getActualUserId());

        UserAccount user = bizUserAccountService.addDeviceUserAccount(userDevice);

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.APP.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        //把token mqtt推送到硬件设备UserAccount
        mqttPushService.pushLoginToken(userDevice, LoginVo.<UserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .token(token)
                .params(userDevice.getHomeId())
                .build());

        userDevice = userDeviceService.getById(dto.getDeviceId());
        JSONObject params = new JSONObject();
        params.put("device_id", userDevice.getDeviceId());
        params.put("product_id", userDevice.getProductId());
        params.put("device_name", userDevice.getDeviceName());
        params.put("room_id", userDevice.getHomeId() + "");

        apiConfigService.sendApiConfigData(params, "/device/push/device");

        //主控设备下的所有离线设备上线（排除mesh类型及虚设备）
        UserDevice masterDevice = userDeviceService.getById(userDevice.getDeviceId());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("hotel进入系统推送sleep30秒===>");
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
        }).start();

        return CommonResultVo.SUCCESS(token);
    }

    /**
     * 新增设备
     *
     * @param dto
     * @return
     */
    @PostMapping("add")
    //@CustomPermissions("user_device:add")
    public CommonResultVo<UserDevice> add(@RequestBody @Valid UserDeviceAddDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.hoteladd(dto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 主控设备重启
     *
     * @param deviceIdDto
     * @return
     */
    @PostMapping("restart_master")
    @CustomPermissions("user_device:restart_master")
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
    @PostMapping("reset_mesh")
    @CustomPermissions("user_device:reset_mesh")
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
    @PostMapping("search_new_mesh")
    @CustomPermissions("user_device:search_new_mesh")
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
    @PostMapping("delete")
    @CustomPermissions("user_device:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid DeviceIdDto dto) {

        UserDevice userDevice = bizUserDeviceService.delete(dto, UserDto.getUser().getActualUserId());

        //主控设备或者主控对应的虚设备 去掉对应的账号，去掉登录token
        if (userDevice.getMasterDeviceId().equals(userDevice.getPhysicalDeviceId())) {
            UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(userDevice.getMasterDeviceId())
                    .build()));
            if (user != null) {
                bizUserAccountService.cancellation(user.getId());
                LoginUtils.logout(UserDto.builder()
                        .platform(PlatFormEnum.APP.getCode())
                        .uId(user.getId())
                        .account(user.getMobile())
                        .actualUserId(user.getActualUserId())
                        .build());
            }
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑设备
     *
     * @param dto
     * @return
     */
    @PostMapping("edit")
    @CustomPermissions("user_device:edit")
    public CommonResultVo<String> edit(@RequestBody @Valid UserDeviceEditDto dto) {
        bizUserDeviceService.hotelEdit(Collections.singletonList(dto), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 编辑设备
     *
     * @param dto
     * @return
     */
    @PostMapping("edit_batch")
    //@CustomPermissions("user_device:edit_batch")
    public CommonResultVo<String> editBatch(@RequestBody @Valid UserDeviceEditBatchDto dto) {
        bizUserDeviceService.edit(dto.getList(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询设备详情数据
     *
     * @param dto
     * @return
     */
    @GetMapping("info")
    @CustomPermissions("user_device:info")
    public CommonResultVo<UserDevice> info(@Valid DeviceIdDto dto) {
        return CommonResultVo.SUCCESS(userDeviceService.queryInfo(dto));
    }

    /**
     * 测试发送红外码
     *
     * @param dto
     * @return
     */
    @PostMapping("test_ir_data")
    @CustomPermissions("user_device:test_ir_data")
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
    @PostMapping("send_data")
    @CustomPermissions("user_device:send_data")
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
    @PostMapping("study_rf_data")
    @CustomPermissions("user_device:study_rf_data")
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
    @PostMapping("study_rf_data2")
    @CustomPermissions("user_device:study_rf_data2")
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
    @RequestMapping("scene_add_device_list")
    @CustomPermissions("user_device:scene_add_device_list")
    public CommonResultVo<List<SceneAddDeviceListVo>> sceneAddDeviceList(SceneAddDeviceDto dto) {
        // authIsAdminUtil.checkHomeId(dto.getHomeId());
        return CommonResultVo.SUCCESS(bizUserDeviceService.sceneAddDeviceList(dto, UserDto.getUser().getActualUserId()));
    }


    /**
     * 获取控制器设备
     *
     * @param controlDeviceto
     * @return
     */
    @RequestMapping("list_control_device")
    @CustomPermissions("user_device:list_control_device")
    public CommonResultVo<List<UserDevice>> listControlDevice(@RequestBody @Valid ControlDeviceto controlDeviceto) {
        return CommonResultVo.SUCCESS(userDeviceService.listControlDevice(controlDeviceto));
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

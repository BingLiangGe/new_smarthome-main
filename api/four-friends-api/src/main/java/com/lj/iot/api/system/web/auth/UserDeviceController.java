package com.lj.iot.api.system.web.auth;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.BrandTypeVo;
import com.lj.iot.biz.base.vo.MasterDeviceDto;
import com.lj.iot.biz.base.vo.ProductListVo;
import com.lj.iot.biz.base.vo.UserDeviceVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.base.dto.SendDataDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/auth/user_device")
public class UserDeviceController {


    @Resource
    IUserDeviceService userDeviceService;

    @Autowired
    private BizProductService bizProductService;

    @Resource
    IIrBrandTypeService irBrandTypeService;

    @Resource
    IIrModelService irModelService;

    @Resource
    BizProductThingModelKeyService bizProductThingModelKeyService;

    @Resource
    BizIrDeviceService bizIrDeviceService;

    @Resource
    BizUserDeviceService bizUserDeviceService;


    @Autowired
    private IHotelFloorHomeService hotelFloorHomeService;

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private MqttPushService mqttPushService;

    @Autowired
    private IEntityAliasService entityAliasService;

    @Resource
    IProductService productService;


    /**
     * 设置音量
     *
     * @return
     */
    @RequestMapping("/settingVolume")
    public CommonResultVo<String> settingVolume(@RequestBody DeviceVolumeVo vo) {
        log.info("deviceId={},value={}", vo.getDeviceId(), vo.getValue());

        UserDevice userDevice = userDeviceService.getById(vo.getDeviceId());

        ValidUtils.isNullThrow(userDevice, "设备不存在");


        if (vo.getValue() < 0 || vo.getValue() > 100) {
            ValidUtils.isNullThrow(null, "音量仅支持0-100范围调节");
        }

        if (!userDevice.getMasterDeviceId().equals(vo.getDeviceId())) {
            ValidUtils.isNullThrow(null, "非主控不可执行该操作");
        }


        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_SUB_DEVICE_NETWORK, userDevice.getProductId(), userDevice.getDeviceId());

        JSONObject respJson = new JSONObject();

        respJson.put("vol", vo.getValue());
        MQTT.publish(topic, respJson.toJSONString());
        log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(respJson));

        userDeviceService.updateById(UserDevice.builder()
                .deviceId(userDevice.getDeviceId())
                .volume(vo.getValue()).build());

        return CommonResultVo.SUCCESS();
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
     * 删除设备
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    //@CustomPermissions("user_device:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid DeviceIdDto dto) {

        UserDevice userDevice = bizUserDeviceService.deleteFourFriend(dto, UserDto.getUser().getActualUserId());

        //主控设备或者主控对应的虚设备 去掉对应的账号，去掉登录token
        if (userDevice.getMasterDeviceId().equals(userDevice.getPhysicalDeviceId())) {
            UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(userDevice.getMasterDeviceId())
                    .build()));
            if (user != null) {
                bizUserAccountService.cancellation(user.getId());
            }
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 详情
     *
     * @return
     */
    @RequestMapping("productInfo")
    public CommonResultVo<Product> info(ProductIdDto dto) {
        return CommonResultVo.SUCCESS(productService.getById(dto.getProductId()));
    }


    /**
     * 分页
     *
     * @param deviceType
     * @return
     */
    @RequestMapping("/aliasList")
    public CommonResultVo<List<EntityAlias>> device(@RequestParam("deviceType") String deviceType, Integer isAll) {

        if (isAll != null && "light".equals(deviceType)) {
            return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                    .attrType("device")
                    .subDeviceType(deviceType)
                    .entityKey("1")
                    .build())));
        }

        return CommonResultVo.SUCCESS(entityAliasService.list(new QueryWrapper<>(EntityAlias.builder()
                .attrType("device")
                .subDeviceType(deviceType)
                .build())));
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
        bizUserDeviceService.editFourFriends(dto.getList(), UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 发送数据
     *
     * @param dto
     * @return
     */
    @PostMapping("send_data")
    //@CustomPermissions("user_device:send_data")
    public CommonResultVo<String> sendData(@RequestBody @Valid SendDataDto dto) {
        bizUserDeviceService.sendData(dto, OperationEnum.APP_C);
        return CommonResultVo.SUCCESS();
    }


    /**
     * 设备详情
     * @param dto
     * @return
     */
    @GetMapping("info")
   // @CustomPermissions("user_device:info")
    public CommonResultVo<UserDevice> info(@Valid DeviceIdDto dto) {
        return CommonResultVo.SUCCESS(userDeviceService.queryInfo(dto));
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

    /**
     * 主控设备添加
     *
     * @param dto
     * @return
     */
    @PostMapping("add_master")
    //@CustomPermissions("user_device:add_master")
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

        //把token mqtt推送到硬件设备
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

        try {
            String result = OkHttpUtils.postJson("http://admin.hs499.com/device/push/device", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public CommonResultVo<UserDevice> add(@RequestBody @Valid UserDeviceAddDto dto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.hoteladd(dto, UserDto.getUser().getActualUserId()));
    }


    /**
     * 测试发送红外码
     *
     * @param dto
     * @return
     */
    @PostMapping("test_ir_data")
    //@CustomPermissions("user_device:test_ir_data")
    public CommonResultVo<String> testIrData(@RequestBody TestIrDataDto dto) {
        bizIrDeviceService.testIrData(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 产品按键数据
     *
     * @param dto
     * @return
     */
    @RequestMapping("keyList")
    public CommonResultVo<List<ProductThingModelKey>> list(@Valid DeviceDto dto) {
        return CommonResultVo.SUCCESS(bizProductThingModelKeyService.keyList(dto));
    }

    /**
     * 红外型号
     * @param dto
     * @return
     */
    @RequestMapping("irModelList")
    public CommonResultVo<List<IrModel>> list(@Valid IrModelDto dto) {
        return CommonResultVo.SUCCESS(irModelService.list(new QueryWrapper<>(IrModel.builder()
                .deviceTypeId(dto.getDeviceTypeId())
                .brandId(dto.getBrandId())
                .build())));
    }


    /**
     * 查询红外设备品牌列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("irBrandList")
    public CommonResultVo<List<BrandTypeVo>> list(@Valid IrBrandTypeDto dto) {
        return CommonResultVo.SUCCESS(irBrandTypeService.listByDeviceTypeId(dto.getDeviceTypeId()));
    }


    /**
     * 查询家下面的主控设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("master_list")
    //@CustomPermissions("user_device:master_list")
    public CommonResultVo<List<UserDevice>> masterList(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(userDeviceService.masterHotelList(dto));
    }

    /**
     * 列表
     *
     * @return
     */
    @RequestMapping("list")
    public CommonResultVo<List<ProductListVo>> list() {
        return CommonResultVo.SUCCESS(bizProductService.hotelListProductListVo());
    }

    /**
     * 查询家下首页显示设备列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("page")
    //@CustomPermissions("user_device:page")
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
}

package com.lj.iot.biz.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.constant.RedisTopicConstant;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.*;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.*;
import com.lj.iot.biz.service.aiui.DeviceNotificationService;
import com.lj.iot.biz.service.enums.OfflineTypeEnum;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.biz.service.mahjong.HeatingTableUtil;
import com.lj.iot.biz.service.mahjong.MahjongMachineUtil;
import com.lj.iot.biz.service.mahjong.MahjongMachineVoiceUtil;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.base.constant.RedisConstant;
import com.lj.iot.common.base.dto.*;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.DeviceIdUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户设备
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@Slf4j
@Service
public class BizUserDeviceServiceImpl implements BizUserDeviceService {
    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IProductModeService productModeService;

    @Autowired
    private IUserDeviceModeService userDeviceModeService;

    @Autowired
    private IProductInventedService productInventedService;

    @Autowired
    private ISceneDeviceService sceneDeviceService;

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;

    @Autowired
    private IHomeRoomService homeRoomService;

    @Resource
    IProductService productService;

    @Resource
    BizSceneService bizSceneService;

    @Resource
    IProductTypeService deviceTypeService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IRfBrandService rfBrandService;

    @Autowired
    private IRfModelService rfModelService;

    @Autowired
    private IIrBrandTypeService irBrandTypeService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IIrModelService irModelService;

    @Resource
    MqttPushService mqttPushService;

    @Resource
    BizUploadEntityService bizUploadEntityService;

    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IProductThingModelKeyService productThingModelKeyService;

    @Autowired
    private BizIrDeviceService bizIrDeviceService;

    @Autowired
    private BizRfDeviceService bizRfDeviceService;

    @Autowired
    private BizUserDeviceScheduleService bizUserDeviceScheduleService;

    @Autowired
    private BizClockService bizClockService;

    @Resource
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IApiConfigService apiConfigService;

    @Autowired
    private IDeviceGroupService deviceGroupService;
    public String url = "/device/push/device";

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IHomeService homeService;

    @Autowired
    private ICacheService cacheService;

    @Autowired
    private IDeviceRecordService deviceRecordService;

/*    @Autowired
    private EmqxConnection emqxConnection;*/

    @DSTransactional
    @Override
    public UserDevice add(UserDeviceAddDto dto, String userId) {

        //主控在线判断
        UserDevice masterUserDevice = userDeviceService.masterStatus(dto.getMasterDeviceId(), userId);

        Product product = productService.getById(dto.getProductId());
        ValidUtils.isNullThrow(product, "产品数据不存在");

        SignalEnum signalEnum = SignalEnum.parse(product.getSignalType());
        ValidUtils.isNullThrow(signalEnum, "暂不支持该种信号设备");

        //控制器设备需要验证控制器设备与主控设备是不是在线
        userDeviceService.controlDeviceStatus(dto.getControlDeviceId(), userId);
        switch (signalEnum) {
            case IR:
            case RF:
                return addIrOrRfDevice(masterUserDevice, product, dto);
            case MESH:
                return addMesh(masterUserDevice, dto);
            default:
                throw CommonException.FAILURE("暂不支持该种信号设备");
        }
    }

    @Override
    public UserDevice addDeviceTest(UserDeviceAddDto dto, String userId) {
        //主控在线判断
        UserDevice masterUserDevice = userDeviceService.masterStatus(dto.getMasterDeviceId(), userId);

        Product product = productService.getById(dto.getProductId());
        ValidUtils.isNullThrow(product, "产品数据不存在");

        SignalEnum signalEnum = SignalEnum.parse(product.getSignalType());
        ValidUtils.isNullThrow(signalEnum, "暂不支持该种信号设备");

        //控制器设备需要验证控制器设备与主控设备是不是在线
        userDeviceService.controlDeviceStatus(dto.getControlDeviceId(), userId);
        switch (signalEnum) {
            case IR:
            case RF:
                return addIrOrRfDevice(masterUserDevice, product, dto);
            case MESH:
                return addMeshTest(masterUserDevice, dto);
            default:
                throw CommonException.FAILURE("暂不支持该种信号设备");
        }
    }


    @DSTransactional
    @Override
    public UserDevice hoteladd(UserDeviceAddDto dto, String userId) {

        //主控在线判断
        UserDevice masterUserDevice = userDeviceService.masterStatus(dto.getMasterDeviceId(), userId);

        Product product = productService.getById(dto.getProductId());
        ValidUtils.isNullThrow(product, "产品数据不存在");

        SignalEnum signalEnum = SignalEnum.parse(product.getSignalType());
        ValidUtils.isNullThrow(signalEnum, "暂不支持该种信号设备");

        //控制器设备需要验证控制器设备与主控设备是不是在线
        userDeviceService.controlDeviceStatus(dto.getControlDeviceId(), userId);
        switch (signalEnum) {
            case IR:
                return hotleaddIrOrRfDevice(masterUserDevice, product, dto);
            case RF:
                return hotleaddIrOrRfDevice(masterUserDevice, product, dto);
            case MESH:
                return hotleaddMesh(masterUserDevice, dto);
            default:
                throw CommonException.FAILURE("暂不支持该种信号设备");
        }


    }


    @DSTransactional
    public Boolean pushLock(UserDeviceAddDto dto) {
        if (dto.getProductId().equals("9337719") & dto.getDeviceId().equals("123456") | dto.getProductId().equals("9337720") & dto.getDeviceId().equals("123456")) {

            Product product = productService.getById(dto.getProductId());
            ValidUtils.isNullThrow(product, "产品不存在");

            String batchCode = IdUtils.nextId();
            Device build = Device.builder().id(DeviceIdUtils.hexId()).productId(dto.getProductId()).CCCFDF(IdUtils.uuid()).batchCode(batchCode).build();
            UserDevice build1 = UserDevice.builder().deviceId(build.getId()).productId(build.getProductId()).physicalDeviceId(build.getId()).build();

            deviceService.save(build);

            //生成文件并上传给EMQX*********//

            List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder().batchCode(batchCode).build()));
            List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder().user_id(device.getId()).password_hash(device.getCCCFDF()).build()).collect(Collectors.toList());

            //创建json文件
            String path = "/data/service/system-api/mqtt/inemqx.json";
            try {
                createTxt(JSON.toJSONString(deviceJsonVoList), path);
                //上传给emqx
                import_users(path);
                JSONObject params = new JSONObject();
                params.put("productId", dto.getProductId());
                params.put("deviceId", "123456");
                params.put("sha256", deviceService.sha256("123456"));
                mqttPushService.addTopology(build1, params);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /* 文件上传到emqx
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
    public String import_users(String path) throws IOException {
        //账号密码
        String credential = Credentials.basic("admin", "public");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("filename", path, RequestBody.create(MediaType.parse("application/octet-stream"), new File(path))).build();
        Request request = new Request.Builder().url("http://47.107.86.36:18083/api/v5/authentication/password_based%3Abuilt_in_database/import_users").method("POST", body).header("Authorization", credential).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response.body().string();
    }

    /* 生成txt文件
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
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
    }


    private UserDevice hotleaddMesh(UserDevice masterUserDevice, UserDeviceAddDto dto) {

        //设备未被绑定
        UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());


        if (userDevice != null) {
            HotelUserAccount beforUser = hotelUserAccountService.getById(userDevice.getUserId());

            if (beforUser != null) {
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            } else {
                UserAccount beforUser2 = userAccountService.getById(userDevice.getUserId());
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser2.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            }
        }

        //如果设备ID不为空，表示精确绑定该设备
        if (StringUtils.isNotBlank(dto.getDeviceId())) {
            //  酒店特定 锁判断
            if (pushLock(dto)) {
                return userDevice;
            }

            Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().productId(dto.getProductId()).id(dto.getDeviceId()).build()));
            ValidUtils.isNullThrow(device, "设备不存在");

           /* HotelUserAccount hotelUser= hotelUserAccountService.getById(masterUserDevice.getUserId());


            // 工作人员绑定，记录三元组
            if (hotelUser != null && hotelUser.getIsWorker() == 1){

               TestCCCFDF testCCCFDF=  testCCCFDFService.getOne(new QueryWrapper(TestCCCFDF.
                        builder().
                        CCCFDF(device.getCCCFDF()).build()));

               if (testCCCFDF!=null ){
                   ValidUtils.isFalseThrow(false, "该三元组已烧录deviceId:"+testCCCFDF.getDeviceId());
               }

                 testCCCFDF=TestCCCFDF.builder()
                        .CCCFDF(device.getCCCFDF())
                        .userId(hotelUser.getId())
                         .deviceId(device.getId())
                        .createTime(LocalDateTime.now()).build();

                testCCCFDFService.save(testCCCFDF);
            }*/


            //如果saveMesh是true 表示直接持久化
            if (dto.getSaveMesh() != null && dto.getSaveMesh()) {
                userDevice = hotelAddMesh(masterUserDevice, device.getId());

                return userDevice;
            }

            JSONObject params1 = new JSONObject();
            params1.put("device_id", device.getId());
            params1.put("product_id", device.getProductId());
            Product byId = productService.getById(device.getProductId());
            params1.put("device_name", byId.getProductName());
            params1.put("room_id", masterUserDevice.getHomeId() + "");
            apiConfigService.sendApiConfigData(params1, url);

            JSONObject params = new JSONObject();
            params.put("productId", device.getProductId());
            params.put("deviceId", device.getId());
            params.put("sha256", deviceService.sha256(device.getId()));
            mqttPushService.addTopology(masterUserDevice, params);

            /*// 发送连接
            if ("heating_table".equals(userDevice.getProductType())) {
                UserDevice finalUserDevice = userDevice;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.info("取暖桌进入等待={}",finalUserDevice);
                            Thread.sleep(1000 * 20L);
                            String commend = HeatingTableUtil.sendMachineData("connection");

                            mqttPushService.pushMeshHeatingTable(masterUserDevice, finalUserDevice, commend, null, null, null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }*/
        } else {
            JSONObject params = new JSONObject();
            params.put("productId", dto.getProductId());
            mqttPushService.addTopology(masterUserDevice, params);
        }

        return null;
    }

    private UserDevice addMeshTest(UserDevice masterUserDevice, UserDeviceAddDto dto) {

        List<DeviceRecord> deviceRecords = deviceRecordService.list(new QueryWrapper<>(DeviceRecord.builder()
                .deviceId(dto.getDeviceId()).build()));

        ValidUtils.isFalseThrow(deviceRecords.isEmpty(), "三元组已重复,deviceId:" + dto.getDeviceId());
        //设备未被绑定
        UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());

        if (userDevice != null) {

            HotelUserAccount beforUser = hotelUserAccountService.getById(userDevice.getUserId());

            if (beforUser != null) {
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            } else {
                UserAccount beforUser2 = userAccountService.getById(userDevice.getUserId());
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser2.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            }
        }

        //如果设备ID不为空，表示精确绑定该设备
        if (StringUtils.isNotBlank(dto.getDeviceId())) {
            Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().productId(dto.getProductId()).id(dto.getDeviceId()).build()));
            ValidUtils.isNullThrow(device, "设备三元组不存在");

            //如果saveMesh是true 表示直接持久化
            if (dto.getSaveMesh() != null && dto.getSaveMesh()) {
                userDevice = addMesh(masterUserDevice, device.getId());

                return userDevice;
            }

            DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("bind").masterDeviceId(device.getId()).homeId(masterUserDevice.getHomeId()).build();

            DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
            deviceNotificationService.handle(notificationDto);

            JSONObject params = new JSONObject();
            params.put("productId", device.getProductId());
            params.put("deviceId", device.getId());
            params.put("sha256", deviceService.sha256(device.getId()));
            mqttPushService.addTopology(masterUserDevice, params);

            /*// 发送连接
            if ("heating_table".equals(userDevice.getProductType())) {
                UserDevice finalUserDevice = userDevice;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.info("取暖桌进入等待={}",finalUserDevice);
                            Thread.sleep(1000 * 20L);
                            String commend = HeatingTableUtil.sendMachineData("connection");

                            mqttPushService.pushMeshHeatingTable(masterUserDevice, finalUserDevice, commend, null, null, null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }*/
        } else {
            JSONObject params = new JSONObject();
            params.put("productId", dto.getProductId());
            mqttPushService.addTopology(masterUserDevice, params);
        }

        return null;
    }


    private UserDevice addMesh(UserDevice masterUserDevice, UserDeviceAddDto dto) {

        //设备未被绑定
        UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());


        if (userDevice != null) {

            HotelUserAccount beforUser = hotelUserAccountService.getById(userDevice.getUserId());

            if (beforUser != null) {
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            } else {
                UserAccount beforUser2 = userAccountService.getById(userDevice.getUserId());
                ValidUtils.noNullThrow(userDevice, "设备已被绑定,account:" + beforUser2.getMobile() + ",deviceId:" + userDevice.getDeviceId());
            }
        }

        //如果设备ID不为空，表示精确绑定该设备
        if (StringUtils.isNotBlank(dto.getDeviceId())) {
            Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().productId(dto.getProductId()).id(dto.getDeviceId()).build()));
            ValidUtils.isNullThrow(device, "设备不存在");

            //如果saveMesh是true 表示直接持久化
            if (dto.getSaveMesh() != null && dto.getSaveMesh()) {
                userDevice = addMesh(masterUserDevice, device.getId());

                return userDevice;
            }

            DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("bind").masterDeviceId(device.getId()).homeId(masterUserDevice.getHomeId()).build();

            DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
            deviceNotificationService.handle(notificationDto);

            JSONObject params = new JSONObject();
            params.put("productId", device.getProductId());
            params.put("deviceId", device.getId());
            params.put("sha256", deviceService.sha256(device.getId()));
            mqttPushService.addTopology(masterUserDevice, params);

            /*// 发送连接
            if ("heating_table".equals(userDevice.getProductType())) {
                UserDevice finalUserDevice = userDevice;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.info("取暖桌进入等待={}",finalUserDevice);
                            Thread.sleep(1000 * 20L);
                            String commend = HeatingTableUtil.sendMachineData("connection");

                            mqttPushService.pushMeshHeatingTable(masterUserDevice, finalUserDevice, commend, null, null, null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }*/
        } else {
            JSONObject params = new JSONObject();
            params.put("productId", dto.getProductId());
            mqttPushService.addTopology(masterUserDevice, params);
        }

        return null;
    }

    @Override
    public void searchNewMesh(SearchMeshDeviceDto dto, String userId) {
        UserDevice masterDevice = userDeviceService.masterStatus(dto.getMasterDeviceId(), userId);
        mqttPushService.searchNewMesh(masterDevice, dto.getProductId());
    }

    private UserDevice editFourFriendsSolo(UserDeviceEditDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        log.info("editFourFriendsSolo,roomId={},homeId={}", dto.getRoomId(), userDevice.getHomeId());

        ValidUtils.isNullThrow(homeRoomService.getOne(new QueryWrapper<>(HomeRoom.builder().id(dto.getRoomId()).homeId(userDevice.getHomeId()).build())), "房间不存在");


        //切换模型
        UserDevice save = UserDevice.builder().deviceId(userDevice.getDeviceId()).customName(dto.getCustomName()).roomId(dto.getRoomId()).doorPwd(dto.getDoorPwd()).build();

        if (dto.getModelId() != null && userDevice.getSignalType().equals(SignalEnum.IR.getCode())) {

            IrModel dbMode = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(userDevice.getModelId()).build()));

            //必须同类型同品牌     比如格力空调-只能切换格力空调的模型
            IrModel model = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(dto.getModelId()).deviceTypeId(dbMode.getDeviceTypeId()).brandId(dbMode.getBrandId()).build()));
            ValidUtils.isNullThrow(model, "模型数据不存在");

            save.setModelId(dto.getModelId());
        }


//        //判断名字是否重复
//        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
//                .customName(dto.getCustomName())
//                .roomId(dto.getRoomId())
//                .build()));
//        ValidUtils.isTrueThrow(list.size()>0, "名字已重复");

        userDeviceService.updateById(save);
        UserDevice byId = userDeviceService.getById(save.getDeviceId());


        // 3326 同步消息-edit
        if (!dto.getCustomName().equals(byId.getCustomName()) || !dto.getRoomId().equals(byId.getRoomId())) {

            // 3326进行分发-bind
            DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("edit").masterDeviceId(byId.getDeviceId()).homeId(byId.getHomeId()).build();

            DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
            deviceNotificationService.handle(notificationDto);
        }


        //设备更新后,把消息发给设备
        String masterProductId = byId.getMasterProductId();
        String masterDeviceId = byId.getMasterDeviceId();

        mqttPushService.pushDevice(byId, masterProductId, masterDeviceId);

        //房间ID 实设备和虚设备都要改成一样的
        /*userDeviceService.update(UserDevice.builder()
                        .roomId(dto.getRoomId())
                        .build(),
                new QueryWrapper<>(UserDevice.builder()
                        .physicalDeviceId(userDevice.getPhysicalDeviceId())
                        .build()));*/

        //离线功能数据
        mqttPushService.pushOfficeData(userDevice.getHomeId(), OfflineTypeEnum.OFFLINE_EDIT, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(userDevice.getDeviceId()).productType(userDevice.getProductType()).topProductType(userDevice.getTopProductType()).signalType(userDevice.getSignalType()).build());

        return userDeviceService.getById(userDevice.getDeviceId());
    }


    private UserDevice edit(UserDeviceEditDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(userId).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        ValidUtils.isNullThrow(homeRoomService.getOne(new QueryWrapper<>(HomeRoom.builder().id(dto.getRoomId()).homeId(userDevice.getHomeId()).userId(userDevice.getUserId()).build())), "房间不存在");


        //切换模型
        UserDevice save = UserDevice.builder().deviceId(userDevice.getDeviceId()).customName(dto.getCustomName()).roomId(dto.getRoomId()).doorPwd(dto.getDoorPwd()).build();

        if (dto.getModelId() != null && userDevice.getSignalType().equals(SignalEnum.IR.getCode())) {

            IrModel dbMode = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(userDevice.getModelId()).build()));

            //必须同类型同品牌     比如格力空调-只能切换格力空调的模型
            IrModel model = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(dto.getModelId()).deviceTypeId(dbMode.getDeviceTypeId()).brandId(dbMode.getBrandId()).build()));
            ValidUtils.isNullThrow(model, "模型数据不存在");

            save.setModelId(dto.getModelId());
        }



//        //判断名字是否重复
//        List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
//                .customName(dto.getCustomName())
//                .roomId(dto.getRoomId())
//                .build()));
//        ValidUtils.isTrueThrow(list.size()>0, "名字已重复");

        userDeviceService.updateById(save);
        UserDevice byId = userDeviceService.getById(save.getDeviceId());


        // 3326 同步消息-edit
        if (!dto.getCustomName().equals(byId.getCustomName()) || !dto.getRoomId().equals(byId.getRoomId())) {

            // 3326进行分发-bind
            DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("edit").masterDeviceId(byId.getDeviceId()).homeId(byId.getHomeId()).build();

            DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
            deviceNotificationService.handle(notificationDto);
        }


        //设备更新后,把消息发给设备
        String masterProductId = byId.getMasterProductId();
        String masterDeviceId = byId.getMasterDeviceId();

        mqttPushService.pushDevice(byId, masterProductId, masterDeviceId);

        //房间ID 实设备和虚设备都要改成一样的
        userDeviceService.update(UserDevice.builder()
                        .roomId(dto.getRoomId())
                        .build(),
                new QueryWrapper<>(UserDevice.builder()
                        .physicalDeviceId(userDevice.getPhysicalDeviceId())
                        .build()));

        //离线功能数据
        mqttPushService.pushOfficeData(userDevice.getHomeId(), OfflineTypeEnum.OFFLINE_EDIT, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(userDevice.getDeviceId()).productType(userDevice.getProductType()).topProductType(userDevice.getTopProductType()).signalType(userDevice.getSignalType()).build());

        return userDeviceService.getById(userDevice.getDeviceId());
    }


    private UserDevice hotleEdit(UserDeviceEditDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(userId).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        ValidUtils.isNullThrow(homeRoomService.getOne(new QueryWrapper<>(HomeRoom.builder().id(dto.getRoomId()).homeId(userDevice.getHomeId()).userId(userDevice.getUserId()).build())), "房间不存在");

        //切换模型
        UserDevice save = UserDevice.builder().deviceId(userDevice.getDeviceId()).customName(dto.getCustomName()).roomId(dto.getRoomId()).build();

        if (dto.getModelId() != null && userDevice.getSignalType().equals(SignalEnum.IR.getCode())) {

            IrModel dbMode = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(userDevice.getModelId()).build()));

            //必须同类型同品牌     比如格力空调-只能切换格力空调的模型
            IrModel model = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(dto.getModelId()).deviceTypeId(dbMode.getDeviceTypeId()).brandId(dbMode.getBrandId()).build()));
            ValidUtils.isNullThrow(model, "模型数据不存在");

            save.setModelId(dto.getModelId());
        }


        userDeviceService.updateById(save);
        UserDevice byId = userDeviceService.getById(dto.getDeviceId());
        //设备更新后,把消息发给设备


        String masterProductId = byId.getMasterProductId();
        String masterDeviceId = byId.getMasterDeviceId();

        mqttPushService.pushDevice(byId, masterProductId, masterDeviceId);


        //房间ID 实设备和虚设备都要改成一样的
        /*userDeviceService.update(UserDevice.builder()
                        .roomId(dto.getRoomId())
                        .build(),
                new QueryWrapper<>(UserDevice.builder()
                        .physicalDeviceId(userDevice.getPhysicalDeviceId())
                        .build()));*/

        //离线功能数据
        mqttPushService.pushOfficeData(userDevice.getHomeId(), OfflineTypeEnum.OFFLINE_EDIT, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(userDevice.getDeviceId()).productType(userDevice.getProductType()).topProductType(userDevice.getTopProductType()).signalType(userDevice.getSignalType()).build());


        JSONObject params = new JSONObject();
        params.put("device_id", userDevice.getDeviceId());
        params.put("product_id", userDevice.getProductId());
        params.put("device_name", userDevice.getDeviceName());
        params.put("room_id", userDevice.getHomeId() + "");
        try {
            apiConfigService.sendApiConfigData(params, url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDeviceService.getById(userDevice.getDeviceId());
    }

    @DSTransactional
    @Override
    public void edit(List<UserDeviceEditDto> dto, String userId) {
        Set<String> productTypeSet = new HashSet<>();
        UserDevice userDevice;
        for (UserDeviceEditDto userDeviceEditDto : dto) {
            userDevice = edit(userDeviceEditDto, userId);
            productTypeSet.add(userDevice.getRealProductType());
        }
        bizUploadEntityService.uploadDeviceNameUserLevel(userId, productTypeSet);
    }

    @Override
    public void editFourFriends(List<UserDeviceEditDto> dto, String userId) {
        Set<String> productTypeSet = new HashSet<>();
        UserDevice userDevice;
        for (UserDeviceEditDto userDeviceEditDto : dto) {
            userDevice = editFourFriendsSolo(userDeviceEditDto, userId);
            productTypeSet.add(userDevice.getRealProductType());
        }
        bizUploadEntityService.uploadDeviceNameUserLevel(userId, productTypeSet);
    }


    @DSTransactional
    @Override
    public void hotelEdit(List<UserDeviceEditDto> dto, String userId) {
        Set<String> productTypeSet = new HashSet<>();
        UserDevice userDevice;
        for (UserDeviceEditDto userDeviceEditDto : dto) {
            userDevice = hotleEdit(userDeviceEditDto, userId);
            productTypeSet.add(userDevice.getRealProductType());
        }
        bizUploadEntityService.uploadDeviceNameUserLevel(userId, productTypeSet);
    }

    @DSTransactional
    @Override
    public UserDevice delete(DeviceIdDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(userId).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        //虚设备是主控的，不允许删，只能靠删除主控来删除主控虚设备
        ValidUtils.isTrueThrow(userDevice.getSignalType().equals(SignalEnum.INVENTED.getCode()) && userDevice.getPhysicalDeviceId().equals(userDevice.getMasterDeviceId()), "主控的灯不允许删除，如果非要删除请删除对应主控");

        delete(userDevice);

        //websocket推送
        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_DELETE, userDevice.getHomeId(), userDevice.getDeviceId());

        return userDevice;
    }

    @DSTransactional
    @Override
    public UserDevice deleteFourFriend(DeviceIdDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(userId).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        //虚设备是主控的，不允许删，只能靠删除主控来删除主控虚设备
        ValidUtils.isTrueThrow(userDevice.getSignalType().equals(SignalEnum.INVENTED.getCode()) && userDevice.getPhysicalDeviceId().equals(userDevice.getMasterDeviceId()), "主控的灯不允许删除，如果非要删除请删除对应主控");

        delete(userDevice);


        return userDevice;
    }


    @DSTransactional
    @Override
    public UserDevice hotleDelete(DeviceIdDto dto, String userId) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(userId).build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        //虚设备是主控的，不允许删，只能靠删除主控来删除主控虚设备
        ValidUtils.isTrueThrow(userDevice.getSignalType().equals(SignalEnum.INVENTED.getCode()) && userDevice.getPhysicalDeviceId().equals(userDevice.getMasterDeviceId()), "主控的灯不允许删除，如果非要删除请删除对应主控");

        //delete(userDevice);

        //websocket推送
        bizWsPublishService.publishAllMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_DELETE, userDevice.getHomeId(), userDevice.getDeviceId());

        return userDevice;
    }

    @DSTransactional
    @Override
    public void delete(UserDevice userDevice) {
        SignalEnum signalEnum = SignalEnum.parse(userDevice.getSignalType());
        switch (signalEnum) {
            case RF:
                UserDevice masterUserDevice3 = userDeviceService.getById(userDevice.getMasterDeviceId());
                if (masterUserDevice3 != null) {
                    //通知网关topology关系
                    mqttPushService.delete(masterUserDevice3, userDevice);
                }
            case IR:
                UserDevice masterUserDevice2 = userDeviceService.getById(userDevice.getMasterDeviceId());
                deleteDevice(userDevice);
                if (masterUserDevice2 != null) {
                    //通知网关topology关系
                    mqttPushService.delete(masterUserDevice2, userDevice);
                }
                break;
            case MESH: {
                UserDevice masterUserDevice = userDeviceService.getById(userDevice.getMasterDeviceId());
                deleteDevice(userDevice);
                if (masterUserDevice != null) {
                    //通知网关topology关系
                    mqttPushService.delete(masterUserDevice, userDevice);
                }
                break;
            }
            case MASTER:
                deleteMasterDevice(userDevice);
                //通知网关topology关系
                mqttPushService.delete(userDevice, userDevice);
                break;
            case INVENTED:
                UserDevice physicalDevice = userDeviceService.getById(userDevice.getPhysicalDeviceId());
                delete(physicalDevice);
                break;
            default:
                throw CommonException.FAILURE("暂不支持该种信号设备");
        }
    }

    private void deleteMasterDevice(UserDevice userDevice) {
        //可能有虚设备
        List<UserDevice> userDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(userDevice.getMasterDeviceId()).build()));
        //获取绑定了该主控设备场景集合
        sceneService.list(new QueryWrapper<>(Scene.builder().homeId(userDevice.getHomeId()).build())).stream().forEach(it -> {
            if (userDevice.getMasterDeviceId().equalsIgnoreCase(it.getMasterId())) {
                //bizSceneService.deleteScene(it.getId(), it.getUserId());
                bizSceneService.deleteHomeScene(it.getId(), it.getUserId());
            } else if (StringUtils.isNotEmpty(it.getMasterId()) && it.getMasterId().contains(userDevice.getMasterDeviceId()) && !it.getMasterId().equals(userDevice.getMasterDeviceId())) {
                String[] split = it.getMasterId().split(",");
                String masterIds = "";
                for (String masterId : split) {
                    if (!masterId.equals(userDevice.getMasterDeviceId())) {
                        masterIds += masterId + ",";
                    }
                }
                if (StringUtils.isNotEmpty(masterIds)) {
                    it.setMasterId(masterIds.substring(0, masterIds.length() - 1));
                }
                //测试有数据已存在的问题
                //sceneService.save(it);
            }
        });
        deleteDeviceBatch(userDeviceList);
    }

    @Override
    public IPage<UserDevice> customPage(UserDevicePageDto pageDto) {
        return userDeviceService.customPage(pageDto);
    }

    @Override
    public UserDeviceStatisticsVo statistics(UserDevicePageDto pageDto) {
        return userDeviceService.statistics(pageDto);
    }

    @DSTransactional
    @Override
    public UserDevice addMasterDevice(MasterDeviceDto masterDeviceDto, String userId) {

        //查询设备是否已经绑定
        UserDevice userDevice = userDeviceService.getById(masterDeviceDto.getDeviceId());
        if (userDevice != null) {
            ValidUtils.isFalseThrow(userDevice.getUserId().equals(userId), "设备已被其他用户绑定");
            return userDevice;
        }

        //查询第一条房间数据
        HomeRoom homeRoom = homeRoomService.first(masterDeviceDto.getHomeId(), userId);
        ValidUtils.isNullThrow(homeRoom, "数据不存在");

        Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().id(masterDeviceDto.getDeviceId()).build()));
        ValidUtils.isNullThrow(device, "主控设备不存在");

        //查询产品表获取产品信息
        Product product = productService.getOne(new QueryWrapper<>(Product.builder().signalType(SignalEnum.MASTER.getCode()).productId(device.getProductId()).build()));
        ValidUtils.isNullThrow(product, "主控产品不存在");

        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());

        userDevice = UserDevice.builder().userId(homeRoom.getUserId()).deviceId(device.getId()).masterDeviceId(masterDeviceDto.getDeviceId()).masterProductId(product.getProductId()).productId(product.getProductId()).homeId(homeRoom.getHomeId()).roomId(homeRoom.getId()).signalType(product.getSignalType()).realProductType(product.getProductType()).productType(product.getProductType()).topProductType(topProductType.getProductType()).physicalDeviceId(device.getId()).imagesUrl(product.getImagesUrl()).status(true).deviceName(product.getProductName()).customName(StringUtils.isBlank(masterDeviceDto.getCustomName()) ? product.getProductName() : masterDeviceDto.getCustomName()).thingModel(product.getThingModel()).build();

        //添加设备
        addUserDevice(userDevice);

        //添加模式数据
        addUserDeviceMode(userDevice);

        //设备未激活则激活
        if (!device.getStatus()) {
            deviceService.updateById(Device.builder().id(device.getId()).status(true).build());
        }

        userDevice = userDeviceService.masterStatus(masterDeviceDto.getDeviceId(), userId);

        bizUserAccountService.addDeviceUserAccount(userDevice);

        return userDeviceService.getById(userDevice.getDeviceId());
    }


    @DSTransactional
    @Override
    public UserDevice hotelAddMasterDevice(MasterDeviceDto masterDeviceDto, String userId) {

        //查询设备是否已经绑定
        UserDevice userDevice = userDeviceService.getById(masterDeviceDto.getDeviceId());
        if (userDevice != null) {
            ValidUtils.isFalseThrow(userDevice.getUserId().equals(userId), "设备已被其他用户绑定");
            return userDevice;
        }

        //查询第一条房间数据
        HomeRoom homeRoom = homeRoomService.first(masterDeviceDto.getHomeId(), userId);
        ValidUtils.isNullThrow(homeRoom, "数据不存在");

        Device device = deviceService.getOne(new QueryWrapper<>(Device.builder().id(masterDeviceDto.getDeviceId()).build()));
        ValidUtils.isNullThrow(device, "主控设备不存在");

        //查询产品表获取产品信息
        Product product = productService.getOne(new QueryWrapper<>(Product.builder().signalType(SignalEnum.MASTER.getCode()).productId(device.getProductId()).build()));
        ValidUtils.isNullThrow(product, "主控产品不存在");

        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());

        userDevice = UserDevice.builder().userId(homeRoom.getUserId()).deviceId(device.getId()).masterDeviceId(masterDeviceDto.getDeviceId()).masterProductId(product.getProductId()).productId(product.getProductId()).homeId(homeRoom.getHomeId()).roomId(homeRoom.getId()).signalType(product.getSignalType()).realProductType(product.getProductType()).productType(product.getProductType()).topProductType(topProductType.getProductType()).physicalDeviceId(device.getId()).imagesUrl(product.getImagesUrl()).status(true).deviceName(product.getProductName()).customName(StringUtils.isBlank(masterDeviceDto.getCustomName()) ? product.getProductName() : masterDeviceDto.getCustomName()).thingModel(product.getThingModel()).build();

        //添加设备
        hotelAddUserDevice(userDevice);

        //添加模式数据
        addUserDeviceMode(userDevice);

        //设备未激活则激活
        if (!device.getStatus()) {
            deviceService.updateById(Device.builder().id(device.getId()).status(true).build());
        }

        userDevice = userDeviceService.masterStatus(masterDeviceDto.getDeviceId(), userId);

        bizUserAccountService.addDeviceUserAccount(userDevice);

        return userDeviceService.getById(userDevice.getDeviceId());
    }

    @Override
    public void restartMasterDevice(DeviceIdDto deviceIdDto, String userId) {
        UserDevice masterUserDevice = userDeviceService.masterStatus(deviceIdDto.getDeviceId(), userId);
        mqttPushService.restartMasterDevice(masterUserDevice);
    }

    /**
     * 删除主控下的mesh设备
     *
     * @param deviceIdDto
     */
    @DSTransactional
    @Override
    public void resetMesh(DeviceIdDto deviceIdDto, String userId) {
        UserDevice masterUserDevice = userDeviceService.masterStatus(deviceIdDto.getDeviceId(), userId);
        List<UserDevice> meshList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().masterDeviceId(masterUserDevice.getDeviceId()).signalType(SignalEnum.MESH.getCode()).build()));
        List<UserDevice> userDeviceList = new ArrayList<>();
        for (UserDevice userDevice : meshList) {
            userDeviceList.addAll(userDeviceService.list(new QueryWrapper<>(UserDevice.builder().physicalDeviceId(userDevice.getDeviceId()).build())));
        }

        deleteDeviceBatch(userDeviceList);

        mqttPushService.reset(masterUserDevice);
    }

    @Override
    public void sendData(SendDataDto dto, OperationEnum operationEnum) {
        UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());

        // 门锁特殊处理
        /*if ("gate_lock".equals(userDevice.getProductType()) || "room_lock".equals(userDevice.getProductType())) {

            cacheService.del("app" + RedisConstant.wait_lock_device + "player_" + dto.getDeviceId());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < 3; i++) {
                            Thread.sleep(1000 * 10);

                            String key = "app" + RedisConstant.wait_lock_device + "player_" + dto.getDeviceId();
                            log.info("门锁重发,key={}", key);
                            String data = cacheService.get(key);
                            log.info("门锁重发,data={}", data);

                            if (data != null) {
                                break;
                            }
                            handle(userDevice, dto.getThingModel(), dto.getKeyCode(), operationEnum);
                        }

                    } catch (Exception e) {
                        log.info("门锁重发----->deviceId={}", dto.getDeviceId());
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }*/
        this.handle(userDevice, dto.getThingModel(), dto.getKeyCode(), operationEnum);
    }


    @Override
    public void handleList(List<HandleUserDeviceDto<UserDevice>> handleList, OperationEnum operationEnum) {
        //存放多联多控组ID,多联多控设备只下发一次指令
        List<String> groupLists = new ArrayList<>();
        HashMap<String, List<HandleUserDeviceDto>> map = new HashMap<>();

        List<HandleUserDeviceDto<UserDevice>> airController = Lists.newArrayList();
        for (HandleUserDeviceDto<UserDevice> handleUserDeviceDto : handleList) {


            //判断单火不处理，组装在一起，留这最后处理 只处理单火， 17002943 17002962
            if (handleUserDeviceDto.getUserDevice().getProductId().equals("17002943") | handleUserDeviceDto.getUserDevice().getProductId().equals("17002962")) {
                List<HandleUserDeviceDto> handleUserDeviceDtos = map.get(handleUserDeviceDto.getUserDevice().getPhysicalDeviceId());
                if (handleUserDeviceDtos != null) {
                    handleUserDeviceDtos.add(handleUserDeviceDto);
                    map.put(handleUserDeviceDto.getUserDevice().getPhysicalDeviceId(), handleUserDeviceDtos);
                } else {
                    ArrayList<HandleUserDeviceDto> list = new ArrayList<>();
                    list.add(handleUserDeviceDto);
                    map.put(handleUserDeviceDto.getUserDevice().getPhysicalDeviceId(), list);
                }

                continue;
            } else if ("airControl".equals(handleUserDeviceDto.getUserDevice().getProductType())) {
                airController.add(handleUserDeviceDto);
                continue;
            }

            try {

                //[多联多控是要为开关控制，所以要加上开关属性判断] 已执行过的group直接跳过,没有执行则加入到已执行列表中
                if (StringUtils.isNotEmpty(handleUserDeviceDto.getUserDevice().getGroupId()) && handleUserDeviceDto.getChangeThingModel().getProperties().size() == 1 && handleUserDeviceDto.getChangeThingModel().getProperties().get(0).getIdentifier().contains("powerstate")) {
                    if (groupLists.contains(handleUserDeviceDto.getUserDevice().getGroupId())) {
                        continue;
                    }
                    groupLists.add(handleUserDeviceDto.getUserDevice().getGroupId());
                }

                //异步延时控制设备
                if (handleUserDeviceDto.getDelayedTime() != null) {

                    if (handleUserDeviceDto.getDelayedTime() > 0) {
                        log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在" + handleUserDeviceDto.getDelayedTime() + "延迟执行--------");

                        /*TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                                handleUserDeviceDto.getChangeThingModel(),
                                handleUserDeviceDto.getKeyCode(), operationEnum);

                        DelayTask task=new DelayTask(taskBase,150);

                        delayQueueManager.put(task);*/

                        final Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                            }
                        };
                        timer.schedule(task, handleUserDeviceDto.getDelayedTime());
                    } else {
                        log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在1ms延迟执行--------");

                        /*TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                                handleUserDeviceDto.getChangeThingModel(),
                                handleUserDeviceDto.getKeyCode(), operationEnum);

                        DelayTask task=new DelayTask(taskBase,150);

                        delayQueueManager.put(task);*/

                        final Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                            }
                        };
                        timer.schedule(task, 100);
                    }
                } else {
                    log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在2.5ms延迟执行--------");

                   /* TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                            handleUserDeviceDto.getChangeThingModel(),
                            handleUserDeviceDto.getKeyCode(), operationEnum);

                    DelayTask task=new DelayTask(taskBase,250);

                    delayQueueManager.put(task);*/

                    final Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                        }
                    };
                    timer.schedule(task, 250);
                }


            } catch (Exception e) {
                log.error("BizUserDeviceServiceImpl.handleList", e);
            }

        }

        //单火设备需要组装一起发出去
        for (Map.Entry<String, List<HandleUserDeviceDto>> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            List<HandleUserDeviceDto> list = entry.getValue();
            for (int i = 0; i < list.size(); i++) {

                if ((i + 1) == list.size()) {

                    final Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            handle((UserDevice) list.get(0).getUserDevice(), list.get(0).getChangeThingModel(), list.get(0).getKeyCode(), operationEnum);
                        }
                    };
                    timer.schedule(task, 100);

                } else {
                    List<ThingModelProperty> properties = list.get(0).getChangeThingModel().getProperties();
                    properties.add(list.get(i + 1).getChangeThingModel().getProperties().get(0));
                }

            }
        }

        if (!airController.isEmpty()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        handleAirContrList(airController, operationEnum);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    public void handleAirContrList(List<HandleUserDeviceDto<UserDevice>> handleList, OperationEnum operationEnum) {
        //存放多联多控组ID,多联多控设备只下发一次指令
        List<String> groupLists = new ArrayList<>();
        HashMap<String, List<HandleUserDeviceDto>> map = new HashMap<>();

        for (HandleUserDeviceDto<UserDevice> handleUserDeviceDto : handleList) {


            try {

                //[多联多控是要为开关控制，所以要加上开关属性判断] 已执行过的group直接跳过,没有执行则加入到已执行列表中
                if (StringUtils.isNotEmpty(handleUserDeviceDto.getUserDevice().getGroupId()) && handleUserDeviceDto.getChangeThingModel().getProperties().size() == 1 && handleUserDeviceDto.getChangeThingModel().getProperties().get(0).getIdentifier().contains("powerstate")) {
                    if (groupLists.contains(handleUserDeviceDto.getUserDevice().getGroupId())) {
                        continue;
                    }
                    groupLists.add(handleUserDeviceDto.getUserDevice().getGroupId());
                }

                //异步延时控制设备
                if (handleUserDeviceDto.getDelayedTime() != null) {

                    if (handleUserDeviceDto.getDelayedTime() > 0) {
                        log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在" + handleUserDeviceDto.getDelayedTime() + "延迟执行--------");

                        /*TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                                handleUserDeviceDto.getChangeThingModel(),
                                handleUserDeviceDto.getKeyCode(), operationEnum);

                        DelayTask task=new DelayTask(taskBase,150);

                        delayQueueManager.put(task);*/

                        final Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                            }
                        };
                        timer.schedule(task, handleUserDeviceDto.getDelayedTime());
                    } else {
                        log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在1ms延迟执行--------");

                        /*TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                                handleUserDeviceDto.getChangeThingModel(),
                                handleUserDeviceDto.getKeyCode(), operationEnum);

                        DelayTask task=new DelayTask(taskBase,150);

                        delayQueueManager.put(task);*/

                        final Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                            }
                        };
                        timer.schedule(task, 100);
                    }
                } else {
                    log.info("-------" + handleUserDeviceDto.getUserDevice().getDeviceId() + "正在2.5ms延迟执行--------");

                   /* TaskBase taskBase=new TaskBase(handleUserDeviceDto.getUserDevice(),
                            handleUserDeviceDto.getChangeThingModel(),
                            handleUserDeviceDto.getKeyCode(), operationEnum);

                    DelayTask task=new DelayTask(taskBase,250);

                    delayQueueManager.put(task);*/

                    final Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            handle(handleUserDeviceDto.getUserDevice(), handleUserDeviceDto.getChangeThingModel(), handleUserDeviceDto.getKeyCode(), operationEnum);
                        }
                    };
                    timer.schedule(task, 250);
                }


            } catch (Exception e) {
                log.error("BizUserDeviceServiceImpl.handleList", e);
            }

        }
    }


    /**
     * 发送码操作统一入口
     *
     * @param userDevice
     * @param thingModel
     * @param keyCode
     */
    @Override
    public void handle(UserDevice userDevice, ThingModel thingModel, String keyCode, OperationEnum operationEnum) {

        /*if (!emqxConnection.checkDeviceStatus(userDevice)) {
            ValidUtils.isNullThrow(null, "设备已离线");
        }*/

        SignalEnum signalEnum = SignalEnum.parse(userDevice.getSignalType());
        //空调特殊处理
        if (ProductTypeEnum.AC.getCode().equals(userDevice.getTopProductType())) {
            if (keyCode != null) {
                if (keyCode.equals("temperatureEq") | keyCode.equals("temperatureReduce") | keyCode.equals("temperatureAdd") | keyCode.equals("workmode")) {
                    thingModel.thingModel2Map().get("powerstate").setValue("1");
                }
            }
        }

        ValidUtils.isNullThrow(signalEnum, "不支持该种信号类型");
        switch (signalEnum) {
            case RF:
                bizRfDeviceService.sendRfData(userDevice, thingModel, keyCode);
                break;
            case IR:
                bizIrDeviceService.sendIrData(userDevice, thingModel, keyCode);
                break;
            case MASTER:
            case MESH:
                this.sendMeshData(userDevice, thingModel, operationEnum);
                break;
            case INVENTED:
                UserDevice physicalUserDevice = userDeviceService.getById(userDevice.getPhysicalDeviceId());

                // 场景标记
                if (userDevice.getIsTrigger() != null) {
                    physicalUserDevice.setIsTrigger(userDevice.getIsTrigger());
                }

                if (operationEnum.getCode().equals("THIRD_PARTY")) {
                    physicalUserDevice.setPhysicalDeviceId(userDevice.getDeviceId());
                }
                this.handle(physicalUserDevice, thingModel, keyCode, operationEnum);
                break;
            default:
                throw CommonException.FAILURE("暂不支持该种信号设备");
        }

        //设备控制日志
       /* operationLogService.save(OperationLog.builder()
                .deviceId(userDevice.getDeviceId())
                .action(new Byte("0"))
                .productId(userDevice.getProductId())
                .productType(userDevice.getProductType())
                .customName(userDevice.getCustomName())
                .userId(userDevice.getUserId())
                .masterDeviceId(userDevice.getMasterDeviceId())
                .signalType(userDevice.getSignalType())
                .params(JSON.toJSONString(thingModel))
                .remark(operationEnum.getDesc())
                .build());*/
    }

    private void sendMeshData(UserDevice userDevice, ThingModel properties, OperationEnum operationEnum) {
        ValidUtils.isFalseThrow(userDevice.getStatus(), "设备已离线");
        UserDevice masterDevice = userDeviceService.masterStatus(userDevice.getMasterDeviceId());
        List<HotelUser> list = hotelUserService.list(new QueryWrapper<>(HotelUser.builder().hotelUserId(userDevice.getUserId()).build()));
        if (list.size() > 0) {
            //设备状态上报：
            //请求接口地址：http://admin.hs499.com/device/push/device/status
            JSONObject params = new JSONObject();
            // 第三方接口进来且是主控，传特殊塞进来的需设备id
            if (operationEnum.getCode().equals("THIRD_PARTY") && userDevice.getSignalType().equals("MASTER")) {
                params.put("device_id", userDevice.getPhysicalDeviceId() + "");
                userDevice.setPhysicalDeviceId(userDevice.getDeviceId()); //用完之后重置它的物理id为主控的
            } else {
                params.put("device_id", userDevice.getDeviceId() + "");
            }

            ThingModelProperty powerstate = properties.thingModel2Map().get("powerstate");
            if (powerstate != null) {
                params.put("code", properties.thingModel2Map().get("powerstate").getValue() + "");
            }
            ThingModelProperty powerstate1 = properties.thingModel2Map().get("powerstate_1");
            if (powerstate1 != null) {
                params.put("code", properties.thingModel2Map().get("powerstate_1").getValue() + "");
            }
            ThingModelProperty powerstate2 = properties.thingModel2Map().get("powerstate_2");
            if (powerstate2 != null) {
                params.put("code", properties.thingModel2Map().get("powerstate_2").getValue() + "");
            }
            ThingModelProperty powerstate3 = properties.thingModel2Map().get("powerstate_3");
            if (powerstate3 != null) {
                params.put("code", properties.thingModel2Map().get("powerstate_3").getValue() + "");
            }
            apiConfigService.sendApiConfigData(params, "/device/push/device/status");
        }

        // 彩灯-颜色备份-语音
        if ("light_c".equals(userDevice.getProductType()) && OperationEnum.AI_C.equals(operationEnum)) {

            if (properties.getProperties() != null && !properties.getProperties().isEmpty()) {

                if ("HSVColor".equals(properties.getProperties().get(0).getIdentifier())) {
                    JSONObject resp = JSONObject.parseObject(properties.getProperties().get(0).getValue().toString());

                    userDevice.setSaturation(resp.getInteger("Saturation"));
                    userDevice.setHue(resp.getInteger("Hue"));

                    userDeviceService.updateById(userDevice);
                }
            }
        }
        // 彩灯-亮度取颜色
        if ("light_c".equals(userDevice.getProductType()) && OperationEnum.AI_C.equals(operationEnum)) {


            if (properties.getProperties() != null && !properties.getProperties().isEmpty()) {


                // 之前有设置过颜色
                if (userDevice.getHue() != null && userDevice.getSaturation() != null) {

                    if ("brightness".equals(properties.getProperties().get(0).getIdentifier())) {

                        ThingModelProperty thingModelProperty = userDevice.getThingModel().getProperties().get(userDevice.getThingModel().getProperties().size() - 1);

                        // 存在model类型
                        if ("code".equals(thingModelProperty.getName())) {

                            // hsv 带颜色
                            if ("1".equals(thingModelProperty.getValue())) {
                                JSONObject valueJson = new JSONObject();

                                valueJson.put("Hue", userDevice.getHue());
                                valueJson.put("Saturation", userDevice.getSaturation());
                                valueJson.put("value", properties.getProperties().get(0).getValue());


                                properties.getProperties().get(0).setIdentifier("HSVColor");
                                mqttPushService.pushMeshPropertiesCase(masterDevice, userDevice, properties, operationEnum, valueJson);
                                return;
                            }
                        }
                    }
                }
            }
        }

        // 声控麻将机特殊处理
        if ("mahjong_voice".equals(userDevice.getProductType())) {
            int value = 0;

            String identifier = properties.getProperties().get(0).getIdentifier();

            if ("position".equals(identifier)) {
                value = Integer.valueOf(properties.getProperties().get(0).getValue().toString());
            }

            String[] datas = MahjongMachineVoiceUtil.sendMachineData(identifier, value);

            mqttPushService.pushMethMajongMachine(masterDevice, userDevice, datas, identifier, properties, operationEnum);
            return;
        }


        // 麻将机特殊处理
        if ("mahjong_machine".equals(userDevice.getProductType())) {
            int value = Integer.valueOf(properties.getProperties().get(0).getValue().toString());
            String identifier = properties.getProperties().get(0).getIdentifier();
            String[] datas = MahjongMachineUtil.sendMachineData(identifier, value);

            mqttPushService.pushMethMajongMachine(masterDevice, userDevice, datas, identifier, properties, operationEnum);
            return;
        }

        // 取暖桌特殊处理
        if ("heating_table".equals(userDevice.getProductType())) {
            String identifier = properties.getProperties().get(0).getIdentifier();
            String commend = HeatingTableUtil.sendMachineData(identifier);

            mqttPushService.pushMeshHeatingTable(masterDevice, userDevice, commend, identifier, properties, operationEnum);
            return;
        }

        try {
            if (userDevice.getIsTrigger() != null) {

                bizWsPublishService.publishAllMemberByHomeId(
                        RedisTopicConstant.TOPIC_CHANNEL_DEVICE_PROPERTIES_POST_SOLO,
                        userDevice.getHomeId(), ThingModelWs.builder()
                                .deviceId(userDevice.getDeviceId())
                                .productType(userDevice.getProductType())
                                .topProductType(userDevice.getTopProductType())
                                .signalType(userDevice.getSignalType())
                                .thingModel(properties).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //单火二路和单火三路开关特殊处理
//        if(userDevice.getProductId().equals("17002962")| userDevice.getProductId().equals("17002943")){
//            mqttPushService.pushMeshSwitchProperties(masterDevice, userDevice, properties, operationEnum);
//        }else{
        mqttPushService.pushMeshProperties(masterDevice, userDevice, properties, operationEnum);
//        }
    }

    /**
     * { //属性对象
     * "productId":"56789",
     * "deviceId":"123456",
     * "properties":[{
     * "identifier":"powerstate_1",
     * "value":"0"
     * }],//属性键
     * ...
     * }
     *
     * @param userDevice
     * @param map
     */
    @Override
    public void saveTogetherProperties(UserDevice userDevice, Map<String, Object> map) {
        ThingModel changeThingModel = JSON.parseObject(JSON.toJSONString(map), ThingModel.class);
        userDeviceService.saveChangeThingModel(userDevice, changeThingModel);
    }

    @Override
    public void saveTogetherPropertiesWithJson(UserDevice userDevice, JSONObject paramJson) {
        ThingModel changeThingModel = JSON.parseObject(paramJson.toJSONString(), ThingModel.class);
        userDeviceService.saveChangeThingModel(userDevice, changeThingModel);
    }

    @Override
    public List<UserDeviceBindVo> showBindList(UserDeviceBindDto dto, String userId) {
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().userId(userId).deviceId(dto.getDeviceId()).build()));
        ValidUtils.isNullThrow(userDevice, "数据不存在");
        userDeviceService.masterStatus(userDevice.getMasterDeviceId());
        //通过主控获取所有可以绑定的设备
        List<UserDeviceBindVo> userDeviceBindVos = new ArrayList<>();
        final Map<Long, String> roomMap = homeRoomService.getRoomMap(userDevice.getHomeId());
        //List<UserDevice> userDeviceBindVoList = userDeviceService.listEnableBindDevice(userDevice.getMasterDeviceId());
        //根据设备组ID查找
        List<UserDevice> userDeviceBindVoList = userDeviceService.listEnableBindDevice(userDevice.getMasterDeviceId());

        for (int i = 0; i < userDeviceBindVoList.size(); i++) {
            UserDevice userDevice1 = userDeviceBindVoList.get(i);

            // 窗帘+锁不支持多联多控
            if ("room_lock".equals(userDevice1.getProductType()) || "curtain".equals(userDevice1.getProductType())) {
                continue;
            }

            //去除有组ID的设备
            if (dto.getAction()) {
                if (userDevice1.getGroupId().equals("")) {
                    userDeviceBindVos.add(UserDeviceBindVo.builder().deviceId(userDevice1.getDeviceId()).roomName(roomMap.get(userDevice1.getRoomId())).deviceName(userDevice1.getDeviceName()).customName(userDevice1.getCustomName()).flag(StringUtils.isNotEmpty(userDevice1.getGroupId())).imagesUrl(userDevice1.getImagesUrl()).build());
                }
            } else {
                if (userDevice1.getGroupId().equals("") | userDevice1.getGroupId().equals(userDevice.getGroupId())) {
                    userDeviceBindVos.add(UserDeviceBindVo.builder().deviceId(userDevice1.getDeviceId()).roomName(roomMap.get(userDevice1.getRoomId())).deviceName(userDevice1.getDeviceName()).customName(userDevice1.getCustomName()).flag(StringUtils.isNotEmpty(userDevice1.getGroupId())).imagesUrl(userDevice1.getImagesUrl()).build());
                }
            }

        }
        return userDeviceBindVos;
    }

    @Override
    public List<UserDeviceBindVo> androidshowBindList(UserDeviceBindDto dto) {
        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).build()));
        ValidUtils.isNullThrow(userDevice, "数据不存在");
        userDeviceService.masterStatus(userDevice.getMasterDeviceId());
        //通过主控获取所有可以绑定的设备
        List<UserDeviceBindVo> userDeviceBindVos = new ArrayList<>();
        final Map<Long, String> roomMap = homeRoomService.getRoomMap(userDevice.getHomeId());
        List<UserDevice> userDeviceBindVoList = userDeviceService.listEnableBindDevice(userDevice.getMasterDeviceId());
        if (userDeviceBindVoList.size() > 0) {
            userDeviceBindVos = userDeviceBindVoList.stream().filter(us -> !dto.getDeviceId().equals(us.getDeviceId())).filter(us -> StringUtils.isEmpty(us.getGroupId()) || us.getGroupId().equals(userDevice.getGroupId())).map(it -> UserDeviceBindVo.builder().deviceId(it.getDeviceId()).roomName(roomMap.get(it.getRoomId())).deviceName(it.getDeviceName()).customName(it.getCustomName()).flag(StringUtils.isNotEmpty(it.getGroupId())).imagesUrl(it.getImagesUrl()).build()).collect(Collectors.toList());
        }
        return userDeviceBindVos;
    }


    @Override
    public void switchGroup(SwitchGroupsDto dto, String uId) {
        //查询当前设备信息
        UserDevice bindDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(dto.getDeviceId()).userId(uId).build()));
        ValidUtils.isNullThrow(bindDevice, "所选设备已经不存在");

        List<String> idArray = Lists.newArrayList();
        idArray.add(dto.getDeviceId());

        List<UserDevice> listByIds = userDeviceService.listEnableBindDevice(idArray, uId, bindDevice.getMasterDeviceId());
        ValidUtils.isFalseThrow(listByIds.size() == idArray.size(), "设备已经被删除");

        deviceGroupService.remove(new UpdateWrapper<>(DeviceGroup.builder()
                .deviceId(dto.getDeviceId())
                .groupId(dto.getBeforeGroupId()).build()));

        userDeviceService.update(UserDevice.builder()
                .groupId(dto.getNewGroupId()).build(), new UpdateWrapper<>(UserDevice.builder()
                .deviceId(bindDevice.getDeviceId()).build()));
        deviceGroupService.save(DeviceGroup.builder()
                .groupId(dto.getNewGroupId())
                .deviceId(dto.getDeviceId())
                .userId(uId)
                .createTime(LocalDateTime.now())
                .groupName("分组")
                .build());

        List<UserDevice> beforIds = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .groupId(dto.getBeforeGroupId()).build()));

        List<SendUserDeviceBindVo> sendBeforeBindVos = beforIds.stream().map(it -> {
            SendUserDeviceBindVo sendUserDeviceBindVo = new SendUserDeviceBindVo();
            sendUserDeviceBindVo.setPhysicalDeviceId(it.getPhysicalDeviceId());
            List<ThingModelProperty> thingModelProperties = it.getThingModel().getProperties();
            for (ThingModelProperty thingModelProperty : thingModelProperties) {
                if (thingModelProperty.getIdentifier().contains("powerstate")) {
                    sendUserDeviceBindVo.setIdentifier(thingModelProperty.getIdentifier());
                    break;
                }
            }
            return sendUserDeviceBindVo;
        }).collect(Collectors.toList());


        mqttPushService.sendBindDevice(bindDevice, sendBeforeBindVos, dto.getBeforeGroupId());

        List<UserDevice> newIds = userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .groupId(dto.getNewGroupId()).build()));

        List<SendUserDeviceBindVo> sendNewBindVos = newIds.stream().map(it -> {
            SendUserDeviceBindVo sendUserDeviceBindVo = new SendUserDeviceBindVo();
            sendUserDeviceBindVo.setPhysicalDeviceId(it.getPhysicalDeviceId());
            List<ThingModelProperty> thingModelProperties = it.getThingModel().getProperties();
            for (ThingModelProperty thingModelProperty : thingModelProperties) {
                if (thingModelProperty.getIdentifier().contains("powerstate")) {
                    sendUserDeviceBindVo.setIdentifier(thingModelProperty.getIdentifier());
                    break;
                }
            }
            return sendUserDeviceBindVo;
        }).collect(Collectors.toList());
        mqttPushService.sendBindDevice(bindDevice, sendNewBindVos, dto.getNewGroupId());
    }


    @DSTransactional
    @Override
    public List<String> bindDevice(DeviceBindDto dto, String uId) {
        //查询当前设备信息
        UserDevice bindDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().deviceId(dto.getDeviceId()).userId(uId).build()));
        ValidUtils.isNullThrow(bindDevice, "所选设备已经不存在");

//        //数据已经绑定
//        DeviceGroup deviceGroup = deviceGroupService.getOne(new QueryWrapper<>(DeviceGroup.builder()
//                .deviceId(dto.getDeviceId())
//                .userId(uId)
//                .build()));
//
//        ValidUtils.noNullThrow(deviceGroup, "所选设备已经绑定");
        //校验绑定设备是否为当前用户的设备
        List<UserDevice> listByIds = userDeviceService.listEnableBindDevice(dto.getDeviceIds(), uId, bindDevice.getMasterDeviceId());
        ValidUtils.isFalseThrow(listByIds.size() == dto.getDeviceIds().size(), "设备已经被删除");

        //推送被删除的场景设备到app
        List<SceneDevice> sceneDevices = sceneDeviceService.list(new QueryWrapper<SceneDevice>().in("device_id", dto.getDeviceIds()));
        List<String> nameList = sceneDevices.stream().map(SceneDevice::getDeviceId).collect(Collectors.toList());
        List<String> deviceNames = new ArrayList<>();
        listByIds.stream().filter(us -> nameList.contains(us.getDeviceId())).forEach(it -> {
            deviceNames.add(it.getCustomName());
        });
        //用于判断是否存在不同主控设备数据
        List<SendUserDeviceBindVo> sendUserDeviceBindVos = listByIds.stream().map(it -> {
            SendUserDeviceBindVo sendUserDeviceBindVo = new SendUserDeviceBindVo();
            sendUserDeviceBindVo.setPhysicalDeviceId(it.getPhysicalDeviceId());
            List<ThingModelProperty> thingModelProperties = it.getThingModel().getProperties();
            for (ThingModelProperty thingModelProperty : thingModelProperties) {
                if (thingModelProperty.getIdentifier().contains("powerstate")) {
                    sendUserDeviceBindVo.setIdentifier(thingModelProperty.getIdentifier());
                    break;
                }
            }
            return sendUserDeviceBindVo;
        }).collect(Collectors.toList());
//        ValidUtils.isFalseThrow(flag.get(), "存在不同主控下的设备数据");
        //校验主控设备是否在线
        UserDevice userDevice = userDeviceService.masterStatus(bindDevice.getMasterDeviceId());
        //推送绑定关系数据到主控设备
        if (dto.getAction()) {
            //创建一个组ID，根据数据遍历一下，是否有组ID，如果没有就是新增数据，如果有代表网关没操作成功，重新发送
            ArrayList<DeviceGroup> arrayList = new ArrayList<>();
            String uuid = null;
            for (int i = 0; i < dto.getDeviceIds().size(); i++) {
                //绑定的设备ID
                String deviceIds = dto.getDeviceIds().get(i);
                DeviceGroup one = deviceGroupService.getOne(new QueryWrapper<>(DeviceGroup.builder().deviceId(deviceIds).build()));
                if (one != null) {
                    uuid = one.getGroupId();
                }
            }
            if (uuid == null) {
                uuid = IdUtils.sId();
                for (int i = 0; i < dto.getDeviceIds().size(); i++) {
                    //绑定的设备ID
                    String deviceIds = dto.getDeviceIds().get(i);
                    DeviceGroup one = new DeviceGroup();
                    one.setDeviceId(deviceIds);
                    one.setGroupId(uuid);
                    one.setCreateTime(LocalDateTime.now());
                    one.setUserId(userDevice.getUserId());
                    arrayList.add(one);
                }

            } else {
                //组ID已经存在了，需要把旧的数据给清掉，绑定新的
                List<DeviceGroup> list = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(uuid).build()));
                deviceGroupService.removeBatchByIds(list);
                for (int i = 0; i < dto.getDeviceIds().size(); i++) {
                    //绑定的设备ID
                    String deviceIds = dto.getDeviceIds().get(i);
                    DeviceGroup one = new DeviceGroup();
                    one.setDeviceId(deviceIds);
                    one.setGroupId(uuid);
                    one.setCreateTime(LocalDateTime.now());
                    one.setUserId(userDevice.getUserId());
                    arrayList.add(one);

                    UpdateWrapper updateWrapper = new UpdateWrapper();

                    updateWrapper.eq("device_id", deviceIds);
                    updateWrapper.set("group_id", uuid);

                    userDeviceService.update(updateWrapper);
                }
            }
            deviceGroupService.saveOrUpdateBatch(arrayList);
            mqttPushService.sendBindDevice(userDevice, sendUserDeviceBindVos, arrayList.get(0).getGroupId());
        } else {
            String groupId = bindDevice.getGroupId();
            ValidUtils.isEmptyThrow(groupId, "数据不存在");
            mqttPushService.sendBindDevice(userDevice, sendUserDeviceBindVos, bindDevice.getGroupId());
        }
        return deviceNames;
    }

    @DSTransactional
    @Override
    public void updateBindDevice(UserDevice userDevice, Object data) {
        Map<String, Object> deviceData = (Map<String, Object>) data;
        String groupId = (String) deviceData.get("groupId");

        DeviceGroup deviceGroup = new DeviceGroup();
        deviceGroup.setGroupId(groupId);
        List<DeviceGroup> groupList = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().userId(userDevice.getUserId()).build()).groupBy("group_id"));

        List<DeviceGroup> delGroupList = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(groupId).build()));
        for (int i = 0; i < delGroupList.size(); i++) {
            sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder().deviceId(delGroupList.get(i).getDeviceId()).build()));

        }
        //让用户设备表同步
        if (delGroupList.size() == 0) {
            List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().groupId(groupId).build()));
            for (int i = 0; i < list.size(); i++) {
                UserDevice user = list.get(i);
                user.setGroupId("");
                userDeviceService.updateById(user);
            }
        }


        //删除旧有的数据
        deviceGroupService.remove(new QueryWrapper<>(deviceGroup));
        userDeviceService.update(UserDevice.builder().groupId("").build(), new UpdateWrapper<UserDevice>().eq("group_Id", groupId));
        if (deviceData.get("deviceList") != null) {
            List<DeviceGroup> deviceList = new ArrayList<DeviceGroup>();
            List<Map<String, String>> deviceLists = (List<Map<String, String>>) deviceData.get("deviceList");
            for (int i = 0; i < deviceLists.size(); i++) {
                Map<String, String> stringStringLinkedHashMap = deviceLists.get(i);
                String physicalDeviceId = stringStringLinkedHashMap.get("physicalDeviceId");
                String identifier = stringStringLinkedHashMap.get("identifier");
                UserDevice us = userDeviceService.matchUserDevice(physicalDeviceId, identifier);
                if (us != null) {
                    DeviceGroup build = DeviceGroup.builder().deviceId(us.getDeviceId()).groupId(groupId).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).userId(userDevice.getUserId())
                            //.groupName("分组"+(groupList.size()+1))
                            .groupName("分组").build();
                    deviceList.add(build);
                }
            }
            if (deviceList.size() > 1) {
                //多练多控新增或者删除
                for (int i = 0; i < deviceList.size(); i++) {
                    //更新用户设备的组信息
                    userDeviceService.updateById(UserDevice.builder().groupId(groupId).deviceId(deviceList.get(i).getDeviceId()).build());
                }
                //保存新的组信息
                deviceGroupService.saveBatch(deviceList);
                //新增动作可以删除场景
                for (int i = 0; i < deviceList.size(); i++) {
                    sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder().deviceId(deviceList.get(i).getDeviceId()).build()));
                }
            }
        }
    }

    @DSTransactional
    @Override
    public List<SceneAddDeviceListVo> sceneAddDeviceList(SceneAddDeviceDto dto, String userId) {

        //获取场景所有设备ID
        Set<String> deviceIdSet = new HashSet<>();
        if (dto.getSceneId() != null) {
            Scene scene = sceneService.getOne(new QueryWrapper<>(Scene.builder().id(dto.getSceneId()).homeId(dto.getHomeId()).userId(userId).build()));
            ValidUtils.isNullThrow(scene, "场景不存在");
            deviceIdSet = sceneDeviceService.listDeviceIds(scene.getId());
        }

        List<UserDeviceVo> userDeviceVoList = userDeviceService.showSceneList(HomeRoomIdDto.builder().homeId(dto.getHomeId()).build());

        List<SceneAddDeviceListVo> sceneAddDeviceListVoList = new ArrayList<>();
        SceneAddDeviceListVo addDeviceListVo = new SceneAddDeviceListVo();
        Set<String> flagSet = new HashSet<>();
        for (UserDeviceVo userDeviceVo : userDeviceVoList) {

            // 房间锁不支持情景模式
            if ("room_lock".equals(userDeviceVo.getProductType())) {
                continue;
            }

            if (!flagSet.contains(userDeviceVo.getGroupId())) {
                flagSet.add(userDeviceVo.getGroupId());
                addDeviceListVo = new SceneAddDeviceListVo();
                addDeviceListVo.setCode(userDeviceVo.getGroupId());
                addDeviceListVo.setName(StringUtils.isBlank(userDeviceVo.getGroupId()) ? "默认" : "分组" + flagSet.size());
                addDeviceListVo.setUserDeviceList(new ArrayList<>());
                sceneAddDeviceListVoList.add(addDeviceListVo);
            }

            // 查询场景thinmodel
            try {

                SceneDevice sceneDevice = sceneDeviceService.getOne(new QueryWrapper<>(SceneDevice.builder().sceneId(dto.getSceneId()).deviceId(userDeviceVo.getDeviceId()).build()));
                if (sceneDevice != null) {

                    String value = (String) sceneDevice.getThingModel().getProperties().get(0).getValue();
                    userDeviceVo.setThingModel(sceneDevice.getThingModel());

                    if ("0".equals(value)) {
                        userDeviceVo.setModelStatus("关");
                    } else {
                        userDeviceVo.setModelStatus("开");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            userDeviceVo.setFlag(deviceIdSet.contains(userDeviceVo.getDeviceId()));
            addDeviceListVo.getUserDeviceList().add(userDeviceVo);
        }
        return sceneAddDeviceListVoList;
    }

    @DSTransactional
    @Override
    public void delBindDevice(DelDeviceBindDto dto, String uId) {
        List<UserDevice> userDevices = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().userId(uId).groupId(dto.getGroupId()).build()));
        ValidUtils.listIsEmptyThrow(userDevices, "没有绑定组设备");
        //校验主控设备是否在线
        UserDevice userDevice = userDeviceService.masterStatus(userDevices.get(0).getMasterDeviceId());

        // 清空组信息
        userDevices.forEach(data -> {

            List<SceneDevice> list = sceneDeviceService.list(new QueryWrapper<>(SceneDevice.builder().deviceId(data.getDeviceId()).build()));

            if (!list.isEmpty()) {
                list.forEach(sceneDevice -> {
                    sceneDeviceService.removeById(sceneDevice.getId());
                });
            }

            // 清空组信息
            UpdateWrapper updateWrapper = new UpdateWrapper();

            updateWrapper.eq("device_id", data.getDeviceId());
            updateWrapper.set("group_id", "");

            userDeviceService.update(updateWrapper);
        });

        //推送绑定关系数据到主控设备
        mqttPushService.sendBindDevice(userDevice, null, dto.getGroupId());
        deviceGroupService.remove(new QueryWrapper<>(DeviceGroup.builder().groupId(dto.getGroupId()).build()));
    }

    @DSTransactional
    @Override
    public void sysDelBindDevice(DelDeviceBindDto dto, String uId) {
        List<UserDevice> userDevices = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().groupId(dto.getGroupId()).build()));
        //校验主控设备是否在线
        UserDevice userDevice = userDeviceService.masterStatus(userDevices.get(0).getMasterDeviceId());
        //推送绑定关系数据到主控设备

        mqttPushService.sendBindDevice(userDevice, null, dto.getGroupId());
    }

    @Override
    public void change(String type, String deviceId, String musicId, String volume) {
        UserDevice userDevice = userDeviceService.masterStatus(deviceId);
        mqttPushService.musicChange(userDevice, type, musicId, volume);
    }

    @Override
    public List<UserDevice> getMasterUserDeviceByHomeId(Long homeId, String userId) {
        return userDeviceService.list(new QueryWrapper<>(UserDevice.builder().userId(userId).signalType(SignalEnum.MASTER.getCode()).homeId(homeId).build()));
    }

    @Override
    public List<UserDevice> OfflineList(String masterDeviceId, String deviceId) {
        UserDevice masterDevice = userDeviceService.findDeviceByDeviceIdAndRoomId(masterDeviceId);
        ValidUtils.isNullThrow(masterDevice, "设备数据不存在");
        return userDeviceService.listByCondition(masterDevice.getHomeId(), deviceId);
    }


    /**
     * @param message {
     *                "id": "123", //消息ID
     *                "code": 0, //0:成功  -1:失败
     *                "data": {
     *                "productId": "56789", //产品ID
     *                "deviceId": "123456" //设备ID
     *                },
     *                "msg": "success", //消息描述
     *                }
     */
    @Override
    public synchronized void topologyAddDevice(HandleMessage message) {

        String masterDeviceId = message.getTopicDeviceId();
        JSONObject body = message.getBody();

        UserDevice masterUserDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder().masterDeviceId(masterDeviceId).signalType(SignalEnum.MASTER.getCode()).build()), true);

        Integer ZERO = Integer.parseInt("0");
        if (ZERO.compareTo(body.getInteger("code")) != 0) {
            log.error("BizUserDeviceServiceImpl.topologyAddDevice.设备添加失败：{}", JSON.toJSONString(message));
            bizWsPublishService.publishEditMemberByHomeIdFailure(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD, masterUserDevice.getHomeId(), "");
            return;
        }

        JSONObject data = body.getJSONObject("data");
        String deviceId = data.getString("deviceId");

        addMesh(masterUserDevice, deviceId);
    }


    private UserDevice addMesh(UserDevice masterUserDevice, String deviceId) {

        //添加设备数据
        UserDevice userDevice = new UserDevice();
        DeviceWsVo dw = Optional.ofNullable(userDeviceService.getDeviceWsData(deviceId)).orElse(DeviceWsVo.builder().deviceId(deviceId).build());
        try {
            userDevice = addMeshDevice(masterUserDevice, deviceId);

            log.info("发送连接====>userDevice={}", userDevice);
            // 发送连接
            if ("heating_table".equals(userDevice.getProductType())) {
                UserDevice finalUserDevice = userDevice;
                UserDevice masterDevice = userDeviceService.getById(userDevice.getMasterDeviceId());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.info("取暖桌进入等待={}", finalUserDevice);
                            Thread.sleep(1000 * 30L);

                            for (int i = 0; i < 2; i++) {
                                String commend = HeatingTableUtil.sendMachineData("connection");

                                mqttPushService.pushMeshHeatingTable(masterDevice, finalUserDevice, commend, null, null, null);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }

            if ("room_lock".equals(userDevice.getProductType())) {
                UserDevice finalUserDevice1 = userDevice;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.info("进入门锁控制等待={}");
                            Thread.sleep(1000 * 5);

                            ThingModel lockThingModel = finalUserDevice1.getThingModel();

                            lockThingModel.getProperties().get(0).setValue(1);

                            sendData(SendDataDto.builder()
                                    .deviceId(finalUserDevice1.getDeviceId())
                                    .keyCode("open")
                                    .thingModel(lockThingModel)
                                    .build(), OperationEnum.APP_C);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }

        } catch (CommonException e) {
            log.error("BizUserDeviceServiceImpl.topologyAddDevice.设备添加失败：{}", JSON.toJSONString(e.getMessage()));
            bizWsPublishService.publishEditMemberByHomeIdFailure(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD, masterUserDevice.getHomeId(), dw, e.getMessage());
            throw e;
        }
        bizWsPublishService.publishEditMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD, masterUserDevice.getHomeId(), dw);
        return userDevice;
    }


    private UserDevice hotelAddMesh(UserDevice masterUserDevice, String deviceId) {

        //添加设备数据
        UserDevice userDevice = new UserDevice();
        DeviceWsVo dw = Optional.ofNullable(userDeviceService.getDeviceWsData(deviceId)).orElse(DeviceWsVo.builder().deviceId(deviceId).build());
        try {
            userDevice = addMeshDevice(masterUserDevice, deviceId);
            //设备信息推送：【新增、修改都需要】
            //请求接口地址：http://admin.hs499.com/device/push/device
            //device_id:第三方设备id  对应我们这边库里的device_id
            //product_id:第三方产品id 根据三方提供的列表去归类设备类型
            //device_name:设备名称  对我我们库里的device_name
            //room_id:第三方包间id  根据前面保存的第三方包间id去匹配门店id 包间id然后入库到我们这边

        } catch (CommonException e) {
            log.error("BizUserDeviceServiceImpl.topologyAddDevice.设备添加失败：{}", JSON.toJSONString(e.getMessage()));
            bizWsPublishService.publishEditMemberByHomeIdFailure(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD, masterUserDevice.getHomeId(), dw, e.getMessage());
            throw e;
        }
        bizWsPublishService.publishEditMemberByHomeId(RedisTopicConstant.TOPIC_CHANNEL_DEVICE_ADD, masterUserDevice.getHomeId(), dw);
        return userDevice;
    }

    /**
     * 添加蓝牙设备
     * <p>
     * 1、主控设备属于这个用户
     * 2、设备没有被绑定
     * 3、添加设备：有需设备，分裂  上传语料
     * 4、有模式数据：添加并上传语料
     */
    private UserDevice addMeshDevice(UserDevice masterUserDevice, String deviceId) {

        //查询设备是否已经绑定
        ValidUtils.noNullThrow(userDeviceService.getById(deviceId), "设备已经被绑定");

        Device device = deviceService.getById(deviceId);
        ValidUtils.isNullThrow(device, "设备不存在");

        Product product = productService.getById(device.getProductId());
        ValidUtils.isNullThrow(product, "产品不存在");

        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());

        UserDevice userDevice = UserDevice.builder()
                .userId(masterUserDevice.getUserId())
                .deviceId(device.getId()).masterDeviceId(masterUserDevice.getDeviceId())
                .masterProductId(masterUserDevice.getProductId())
                .productId(product.getProductId())
                .homeId(masterUserDevice.getHomeId()).roomId(masterUserDevice.getRoomId())
                .signalType(product.getSignalType()).realProductType(product.getProductType())
                .productType(product.getProductType()).topProductType(topProductType.getProductType())
                .physicalDeviceId(device.getId()).status(true).imagesUrl(product.getImagesUrl()).deviceName(product.getProductName())
                //.customName(product.getProductName())
                .customName("").statusTime(LocalDateTime.now()).isShowScene(product.getIsShowScene()).thingModel(product.getThingModel()).build();

        //添加设备
        addUserDevice(userDevice);

        //添加模式数据
        addUserDeviceMode(userDevice);

        //生成设备级按键【目前情景设备需要】
        if (product.getIsAddKey()) {
            addUserDeviceMeshKey(userDevice);
        }
        //激活设备
        if (!device.getStatus()) {
            deviceService.updateById(Device.builder().id(device.getId()).status(true).build());
        }

        return userDevice;
    }

    private void addUserDeviceMeshKey(UserDevice userDevice) {
        //获取产品thingmodekey数据
        List<ProductThingModelKey> productThingModelKeys = productThingModelKeyService.keyList(userDevice.getProductId(), userDevice.getModelId() == null ? 0L : userDevice.getModelId());

        List<UserDeviceMeshKey> userDeviceMeshKeys = new ArrayList<>();
        for (ProductThingModelKey productThingModelKey : productThingModelKeys) {
            userDeviceMeshKeys.add(UserDeviceMeshKey.builder().productKeyId(productThingModelKey.getId()).userId(userDevice.getUserId()).deviceId(userDevice.getDeviceId()).keyName(productThingModelKey.getKeyName()).identifier(productThingModelKey.getIdentifier()).productId(userDevice.getProductId()).value(productThingModelKey.getStep()).build());
        }
        if (userDeviceMeshKeys.size() > 0) {
            userDeviceMeshKeyService.saveBatch(userDeviceMeshKeys);
        }
    }

    private UserDevice hotleaddIrOrRfDevice(UserDevice masterUserDevice, Product product, UserDeviceAddDto dto) {
        ValidUtils.isNullThrow(dto.getModelId(), "模型ID不能为空");
        BrandDto brandDto;
        String deviceId = IdUtils.sId();
        if (product.getSignalType().equals(SignalEnum.IR.getCode())) {
            brandDto = addIrDevice(product, dto.getModelId());
        } else {
            //射频按键在这个方法里面处理的
            brandDto = addRfDevice(product, deviceId, masterUserDevice.getUserId(), dto.getModelId());
        }

        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());
        long inRoomId = masterUserDevice.getRoomId();
//        if (dto.getRoomId() != 0) {
//            inRoomId = dto.getRoomId();
//        } else {
//            inRoomId = masterUserDevice.getRoomId();
//        }


        String customName = "";
        if (dto.getCustomName() != null) {
            customName = dto.getCustomName();
        } else {
            customName = "";
        }
        UserDevice userDevice = UserDevice.builder().deviceId(deviceId).physicalDeviceId(deviceId).brandId(brandDto.getBrandId()).modelId(dto.getModelId()).brandName(brandDto.getBrandName()).userId(masterUserDevice.getUserId()).masterDeviceId(masterUserDevice.getDeviceId()).masterProductId(masterUserDevice.getProductId()).productId(product.getProductId()).homeId(masterUserDevice.getHomeId()).roomId(inRoomId).signalType(product.getSignalType()).realProductType(product.getProductType()).topProductType(topProductType.getProductType()).controlProductId(product.getControlProductId()).controlDeviceId(dto.getControlDeviceId()).productType(product.getProductType()).status(true).deviceName(product.getProductName())
                //.customName(product.getProductName())
                .customName(customName).imagesUrl(product.getImagesUrl()).isShowScene(product.getIsShowScene()).thingModel(product.getThingModel()).build();

        //添加设备
        addUserDevice(userDevice);

        //添加模式数据
        addUserDeviceMode(userDevice);


        JSONObject params = new JSONObject();
        params.put("device_id", userDevice.getDeviceId());
        params.put("product_id", userDevice.getProductId());
        params.put("device_name", userDevice.getDeviceName());
        params.put("room_id", userDevice.getHomeId() + "");
        try {
            apiConfigService.sendApiConfigData(params, url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userDevice;
    }


    private UserDevice addIrOrRfDevice(UserDevice masterUserDevice, Product product, UserDeviceAddDto dto) {
        ValidUtils.isNullThrow(dto.getModelId(), "模型ID不能为空");
        BrandDto brandDto;
        String deviceId = IdUtils.sId();
        if (product.getSignalType().equals(SignalEnum.IR.getCode())) {
            brandDto = addIrDevice(product, dto.getModelId());
        } else {
            //射频按键在这个方法里面处理的
            brandDto = addRfDevice(product, deviceId, masterUserDevice.getUserId(), dto.getModelId());
        }

        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());
        long inRoomId = masterUserDevice.getRoomId();
//        if (dto.getRoomId() != 0) {
//            inRoomId = dto.getRoomId();
//        } else {
//            inRoomId = masterUserDevice.getRoomId();
//        }


        String customName = "";
        if (dto.getCustomName() != null) {
            customName = dto.getCustomName();
        } else {
            customName = "";
        }
        UserDevice userDevice = UserDevice.builder().deviceId(deviceId).physicalDeviceId(deviceId).brandId(brandDto.getBrandId()).modelId(dto.getModelId()).brandName(brandDto.getBrandName()).userId(masterUserDevice.getUserId()).masterDeviceId(masterUserDevice.getDeviceId()).masterProductId(masterUserDevice.getProductId()).productId(product.getProductId()).homeId(masterUserDevice.getHomeId()).roomId(inRoomId).signalType(product.getSignalType()).realProductType(product.getProductType()).topProductType(topProductType.getProductType()).controlProductId(product.getControlProductId()).controlDeviceId(dto.getControlDeviceId()).productType(product.getProductType()).status(true).deviceName(product.getProductName())
                //.customName(product.getProductName())
                .customName(customName).imagesUrl(product.getImagesUrl()).isShowScene(product.getIsShowScene()).statusTime(LocalDateTime.now()).thingModel(product.getThingModel()).build();


        //添加设备
        addUserDevice(userDevice);

        //添加模式数据
        addUserDeviceMode(userDevice);


        // 3326进行分发-bind
        DeviceNotificationDto notificationDto = DeviceNotificationDto.builder().intentName("bind").masterDeviceId(deviceId).homeId(masterUserDevice.getHomeId()).build();


        DeviceNotificationService deviceNotificationService = SpringUtil.getBean("deviceNotifi_" + notificationDto.getIntentName(), DeviceNotificationService.class);
        deviceNotificationService.handle(notificationDto);

        return userDevice;
    }


    private void addUserDevice(UserDevice userDevice) {

        List<ProductInvented> productInventedList = productInventedService.list(new QueryWrapper<>(ProductInvented.builder().productId(userDevice.getProductId()).build()));

        List<UserDevice> userDeviceList = new ArrayList<>();

        //有虚设备，主设备不显示
        if (productInventedList.size() != 0) {
            userDevice.setIsShow(false);
        }
        userDeviceList.add(userDevice);

        for (int i = 0; i < productInventedList.size(); i++) {
            ProductInvented productInvented = productInventedList.get(i);
            ProductType productType = deviceTypeService.getOne(new QueryWrapper<>(ProductType.builder().productType(productInvented.getProductType()).build()));

            ProductType topProductType = productTypeService.getTopProductType(productType.getProductType());

            userDeviceList.add(UserDevice.builder().userId(userDevice.getUserId()).deviceId(userDevice.getDeviceId() + "P" + i).productId(userDevice.getProductId()).homeId(userDevice.getHomeId()).roomId(userDevice.getRoomId()).signalType(SignalEnum.INVENTED.getCode()).masterDeviceId(userDevice.getMasterDeviceId()).masterProductId(userDevice.getMasterProductId()).parentId(userDevice.getDeviceId()).physicalDeviceId(userDevice.getPhysicalDeviceId()).realProductType(userDevice.getProductType()).productType(productType.getProductType()).topProductType(topProductType.getProductType()).deviceName(productType.getProductTypeName()).customName("").thingModel(productInvented.getThingModel()).imagesUrl(productType.getImagesUrl()).isShowScene(productInvented.getIsShowScene()).build());
        }

        userDeviceService.saveBatch(userDeviceList);

        //设备实体上传 新增的时候使用的是产品设备名。产品设备名在应用级时已经上传，所有可以去掉
        //bizUploadEntityService.uploadEntityUserLevel(userDevice.getUserId(), DynamicEntitiesNameEnum.DeviceName);

        //添加设备后推送所有主控
        for (UserDevice device : userDeviceList) {
            mqttPushService.pushOfficeData(device.getHomeId(), OfflineTypeEnum.OFFLINE_ADD, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(device.getDeviceId()).productType(device.getProductType()).topProductType(device.getTopProductType()).signalType(device.getSignalType()).build());
        }
    }


    private void hotelAddUserDevice(UserDevice userDevice) {

        List<ProductInvented> productInventedList = productInventedService.list(new QueryWrapper<>(ProductInvented.builder().productId(userDevice.getProductId()).build()));

        List<UserDevice> userDeviceList = new ArrayList<>();

        //有虚设备，主设备不显示
        if (productInventedList.size() != 0) {
            userDevice.setIsShow(false);
            //额外给第三方推送主控设备id
            JSONObject params = new JSONObject();
            params.put("device_id", userDevice.getDeviceId());
            params.put("product_id", userDevice.getProductId());
            params.put("device_name", "主控设备");
            params.put("room_id", userDevice.getHomeId() + "");
            try {
                apiConfigService.sendApiConfigData(params, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userDeviceList.add(userDevice);

        for (int i = 0; i < productInventedList.size(); i++) {
            ProductInvented productInvented = productInventedList.get(i);
            ProductType productType = deviceTypeService.getOne(new QueryWrapper<>(ProductType.builder().productType(productInvented.getProductType()).build()));

            ProductType topProductType = productTypeService.getTopProductType(productType.getProductType());
            String deviceName = productType.getProductTypeName();
            String customName = productType.getProductTypeName();
            if (i == 1) {
                deviceName = "总开关";
                customName = "总开关";
            }
            //TODO 给设备发http
            userDeviceList.add(UserDevice.builder().userId(userDevice.getUserId()).deviceId(userDevice.getDeviceId() + "P" + i).deviceName(deviceName).customName(customName).productId(userDevice.getProductId()).homeId(userDevice.getHomeId()).roomId(userDevice.getRoomId()).signalType(SignalEnum.INVENTED.getCode()).masterDeviceId(userDevice.getMasterDeviceId()).masterProductId(userDevice.getMasterProductId()).parentId(userDevice.getDeviceId()).physicalDeviceId(userDevice.getPhysicalDeviceId()).realProductType(userDevice.getProductType()).productType(productType.getProductType()).topProductType(topProductType.getProductType()).thingModel(productInvented.getThingModel()).imagesUrl(productType.getImagesUrl()).isShowScene(productInvented.getIsShowScene()).build());
            JSONObject params = new JSONObject();
            params.put("device_id", userDevice.getDeviceId() + "P" + i);
            params.put("product_id", userDevice.getProductId());
            params.put("device_name", deviceName);
            params.put("room_id", userDevice.getHomeId() + "");
            try {
                apiConfigService.sendApiConfigData(params, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userDeviceService.saveBatch(userDeviceList);
        //设备实体上传 新增的时候使用的是产品设备名。产品设备名在应用级时已经上传，所有可以去掉
        //bizUploadEntityService.uploadEntityUserLevel(userDevice.getUserId(), DynamicEntitiesNameEnum.DeviceName);

        //添加设备后推送所有主控
        for (UserDevice device : userDeviceList) {
            mqttPushService.pushOfficeData(device.getHomeId(), OfflineTypeEnum.OFFLINE_ADD, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(device.getDeviceId()).productType(device.getProductType()).topProductType(device.getTopProductType()).signalType(device.getSignalType()).build());
        }
    }

    private void addUserDeviceMode(UserDevice userDevice) {
        List<ProductMode> productModeList = productModeService.list(new QueryWrapper<>(ProductMode.builder().productId(userDevice.getProductId()).build()));

        if (productModeList.size() == 0) {
            return;
        }

        List<UserDeviceMode> userDeviceModeList = new ArrayList<>();
        for (ProductMode productMode : productModeList) {
            userDeviceModeList.add(UserDeviceMode.builder().userId(userDevice.getUserId()).deviceId(userDevice.getDeviceId()).productId(userDevice.getProductId()).modeCode(productMode.getModeCode()).modeName(productMode.getModeName()).thingModel(productMode.getThingModel()).build());
        }

        userDeviceModeService.saveBatch(userDeviceModeList);

        //模式实体上传
        bizUploadEntityService.uploadEntityUserLevel(userDevice.getUserId(), DynamicEntitiesNameEnum.Model);
    }

    private BrandDto addIrDevice(Product product, Long modelId) {
        IrModel model = irModelService.getOne(new QueryWrapper<>(IrModel.builder().id(modelId).deviceTypeId(product.getRelationDeviceTypeId()).build()));
        ValidUtils.isNullThrow(model, "模型数据不存在");
        IrBrandType brand = irBrandTypeService.getById(model.getBrandId());
        ValidUtils.isNullThrow(brand, "品牌数据不存在");

        return BrandDto.builder().brandId(brand.getId()).brandName(brand.getBrandName()).build();
    }

    private BrandDto addRfDevice(Product product, String deviceId, String userId, Long modelId) {

        RfModel model = rfModelService.getOne(new QueryWrapper<>(RfModel.builder().id(modelId).deviceTypeId(product.getRelationDeviceTypeId()).build()));
        ValidUtils.isNullThrow(model, "模型数据不存在");

        RfBrand brand = rfBrandService.getById(model.getBrandId());
        ValidUtils.isNullThrow(brand, "品牌数据不存在");

        //查询按键
        List<ProductThingModelKey> productThingModelKeyList = productThingModelKeyService.list(new QueryWrapper<>(ProductThingModelKey.builder().productId(product.getProductId()).build()));

        //保存射频设备按键数据
        for (ProductThingModelKey productThingModelKey : productThingModelKeyList) {
            UserDeviceRfKey userDeviceRfKey = UserDeviceRfKey.builder().userId(userId).deviceId(deviceId).keyId(productThingModelKey.getId()).keyCode(productThingModelKey.getKeyCode()).keyIdx(productThingModelKey.getKeyIdx()).keyName(productThingModelKey.getKeyName()).modelId(modelId).isEffective(false).build();
            userDeviceRfKeyService.save(userDeviceRfKey);
        }

        return BrandDto.builder().brandId(brand.getId()).brandName(brand.getBrandName()).build();
    }

    //scene_device
    //user_device
    //user_clock
    //user_device_mode
    //user_device_rf_key
    //user_device_schedule
    private void deleteDevice(UserDevice userDevice) {
        //可能有虚设备
        List<UserDevice> userDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().physicalDeviceId(userDevice.getDeviceId()).build()));
        //控制器子设备集合
        List<UserDevice> controlUserDeviceList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().controlDeviceId(userDevice.getDeviceId()).build()));

        userDeviceList.addAll(controlUserDeviceList);
        deleteDeviceBatch(userDeviceList);
    }


    //scene_device
    //user_device
    //user_clock
    //user_device_mode
    //user_device_rf_key
    //user_device_mesh_key
    //user_device_schedule
    //device_group
    private void deleteDeviceBatch(List<UserDevice> userDeviceList) {

        for (UserDevice userDevice : userDeviceList) {

            //删除缓存
            userDeviceService.deleteCacheById(userDevice.getDeviceId());

            userDeviceService.remove(new QueryWrapper<>(UserDevice.builder().deviceId(userDevice.getDeviceId()).build()));

            userDeviceModeService.remove(new QueryWrapper<>(UserDeviceMode.builder().deviceId(userDevice.getDeviceId()).build()));

            //测试反馈不要删除 1)
            sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder().deviceId(userDevice.getDeviceId()).build()));
            //删除场景下的主控
            List<Scene> listScene = sceneService.findInSetMasterId(userDevice.getDeviceId());
            if (listScene.size() > 0) {
                for (int i = 0; i < listScene.size(); i++) {
                    String masterId = listScene.get(i).getMasterId();
                    String newmasterId = "";
                    String[] split = masterId.split(",");
                    for (int j = 0; j < split.length; j++) {
                        if (!userDevice.getDeviceId().equals(split[j])) {
                            if (j != split.length) {
                                newmasterId = newmasterId + split[j] + ",";
                            } else {
                                newmasterId = newmasterId + split[j] + ",";
                            }

                        }
                    }
                    listScene.get(i).setMasterId(newmasterId);
                    sceneService.updateById(listScene.get(i));
                }
            }

            userDeviceRfKeyService.remove(new QueryWrapper<>(UserDeviceRfKey.builder().deviceId(userDevice.getDeviceId()).build()));

            userDeviceMeshKeyService.remove(new QueryWrapper<>(UserDeviceMeshKey.builder().deviceId(userDevice.getDeviceId()).build()));

            bizUserDeviceScheduleService.deleteByDeviceId(userDevice.getDeviceId());

            bizClockService.deleteByDeviceId(userDevice.getDeviceId());

            String groupId = userDevice.getGroupId();
            List<DeviceGroup> list1 = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder().groupId(groupId).build()));
            if (list1.size() > 0 & list1.size() <= 2) {
                deviceGroupService.removeBatchByIds(list1);
                for (int i = 0; i < list1.size(); i++) {
                    String deviceId = list1.get(i).getDeviceId();
                    UserDevice user = new UserDevice();
                    user.setGroupId("");
                    user.setDeviceId(deviceId);
                    userDeviceService.updateById(user);
                    sceneDeviceService.remove(new QueryWrapper<>(SceneDevice.builder().deviceId(deviceId).build()));
                }
            } else {
                //删除组
                deviceGroupService.remove(new QueryWrapper<>(DeviceGroup.builder().deviceId(userDevice.getDeviceId()).build()));
                //用户下的设备组要清空
                List<UserDevice> list = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().deviceId(userDevice.getDeviceId()).build()));
                for (int i = 0; i < list.size(); i++) {
                    UserDevice user = list.get(i);
                    user.setGroupId("");
                    userDeviceService.updateById(user);
                }
            }


            //删除设备后推送所有主控
            mqttPushService.pushOfficeData(userDevice.getHomeId(), OfflineTypeEnum.OFFLINE_DELETE, PubTopicEnum.PUB_DEVICE_CHANGE, DeviceChangeDto.builder().deviceId(userDevice.getDeviceId()).productType(userDevice.getProductType()).topProductType(userDevice.getTopProductType()).signalType(userDevice.getSignalType()).build());
        }

        //用户级的自定义设备名和自定义模式名，在删除设备的时候可以不管，aiui中实体词条可以多，不能少
        /*if (userDeviceList.size() != 0) {
            String userId = userDeviceList.get(0).getUserId();
            //设备实体上传
            bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.Model);
            //设备实体上传
            bizUploadEntityService.uploadEntityUserLevel(userId, DynamicEntitiesNameEnum.DeviceName);
        }*/
    }

    @Override
    public void sendVoice(SendVoiceDto dto) {
        ValidUtils.isEmptyThrow(dto.getMasterDeviceId(), "masterDeviceId必传");
        ValidUtils.isEmptyThrow(dto.getCode(), "code必传");

        UserDevice userDevice = userDeviceService.getById(dto.getMasterDeviceId());
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        String url = "https://img.lj-smarthome.com/wechat_mp3/" + dto.getCode() + ".mp3";

        String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, userDevice.getProductId(), dto.getMasterDeviceId());
        String resp = JSON.toJSONString(new HashMap() {{
            put("id", dto.getMasterDeviceId());
            put("url", url);
            put("code", dto.getCode());
        }});
        MQTT.publish(topic, resp);
        log.info("Mqtt-Send:" + topic + "=" + resp);
    }

    @Override
    public void lockMasterDevice(LockMasterDeviceDto dto) {
        ValidUtils.isNullThrow(dto.getHomeId(), "homeId必传");

        Home home = homeService.getById(dto.getHomeId());

        ValidUtils.isNullThrow(dto.getHomeId(), "home信息不存在");

        List<UserDevice> masterList = userDeviceService.list(new QueryWrapper<>(UserDevice.builder().homeId(home.getId()).signalType("MASTER").build()));

        masterList.forEach(masterDevice -> {
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_TRIGGER_CLOCK, masterDevice.getProductId(), masterDevice.getDeviceId());

            // 发送锁定
            MQTT.publish(topic, JSON.toJSONString(new HashMap() {{
                put("id", masterDevice.getDeviceId());

                userDeviceService.updateById(UserDevice.builder().deviceId(masterDevice.getDeviceId()).isDel(dto.getType() == 1 ? true : false).build());
                put("enable", dto.getType());
            }}));
        });
    }
}

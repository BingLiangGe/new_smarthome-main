package com.lj.iot.api.hotel.web.open;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.lj.iot.biz.base.dto.AdminLoginDto;
import com.lj.iot.biz.base.dto.MqttParamDto;
import com.lj.iot.biz.base.vo.AddDeviceJsonVo;
import com.lj.iot.biz.base.vo.ExportDeviceJsonVo;
import com.lj.iot.biz.base.vo.ProductVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.enums.PubTopicEnum;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.base.vo.LoginVo;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.DeviceIdUtils;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.MD5Utils;
import com.lj.iot.common.util.ValidUtils;
import com.lj.iot.common.util.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员 cont
 *
 * @author tyj
 * @date 2023-7-3 11:16:56
 */
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/open/admin")
public class AdminController {

    @Resource
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IProductUpgradeService productUpgradeService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IOperationLogService operationLogService;

    @Autowired
    private IUpgradeRecordService upgradeRecordService;

    @Autowired
    private IHotelService hotelService;

    @Autowired
    private IHotelUserService hotelUserService;

    @Autowired
    private IHotelRoleService hotelRoleService;

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Autowired
    private BizUserAccountService service;

    @Autowired
    private ITripletAccountService tripletAccountService;

    /**
     * 验证状态
     *
     * @param userName
     * @return
     */
    @RequestMapping("/checkTriplet")
    public CommonResultVo<String> checkTriplet(String userName) {
        ValidUtils.isNullThrow(userName, "userName必传");

        List<TripletAccount> list = tripletAccountService.list(new QueryWrapper<>(TripletAccount.builder()
                .accountName(userName)
                .accountStatus(0).build()));

        if (list.isEmpty()) {
            return CommonResultVo.FAILURE();
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 三元组登录
     *
     * @param userName
     * @param userPwd
     * @return
     */
    @RequestMapping("/tripletLogin")
    public CommonResultVo<String> tripletLogin(String userName, String userPwd) {

        ValidUtils.isNullThrow(userName, "userName必传");
        ValidUtils.isNullThrow(userPwd, "userPwd必传");

        List<TripletAccount> list = tripletAccountService.list(new QueryWrapper<>(TripletAccount.builder()
                .accountName(userName)
                .acccountPwd(userPwd)
                .accountStatus(0).build()));

        if (list.isEmpty()) {
            return CommonResultVo.FAILURE_MSG("账号密码有误,或已被冻结!");
        }
        return CommonResultVo.SUCCESS();
    }


    @RequestMapping("/sendTokenComment")
    public CommonResultVo<String> sendTokenComment() {
        service.sendTokenComment();
        return CommonResultVo.SUCCESS();
    }

    @RequestMapping("/addDevice")
    public CommonResultVo<List<AddDeviceJsonVo>> addDevice(String productId, Integer number, HttpServletResponse response) {

        ValidUtils.isNullThrow(productId, "productId为空");

        String batchCode = IdUtils.nextId();
        List<Device> list = Lists.newArrayList();

        List<AddDeviceJsonVo> addDeviceJsonVoList = Lists.newArrayList();
        for (int i = 0; i < number; i++) {

            Device device = Device.builder()
                    .id(DeviceIdUtils.hexId())
                    .productId(productId)
                    .CCCFDF(IdUtils.uuid())
                    .batchCode(batchCode)
                    .build();
            list.add(device);

            addDeviceJsonVoList.add(AddDeviceJsonVo.builder()
                    .id(device.getId())
                    .CCCFDF(device.getCCCFDF())
                    .productId(device.getProductId()).build());
        }
        deviceService.saveBatch(list);

        //生成文件并上传给EMQX*********//
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode(batchCode)
                .build()));
        List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                        .user_id(device.getId())
                        .password_hash(device.getCCCFDF())
                        .build())
                .collect(Collectors.toList());

        //创建json文件
        String path = "/data/service/system-api/mqtt/inemqx.json";

        try {
            createTxt(response, JSON.toJSONString(deviceJsonVoList), path);
            //上传给emqx
            import_users(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResultVo.SUCCESS(addDeviceJsonVoList);
    }

    /* 生成txt文件
     * @author
     * @param	response
     * @param	text 导出的字符串
     * @return
     */
    public void createTxt(HttpServletResponse response, String text, String path) throws IOException {
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

    /**
     * 获取产品列表
     *
     * @return
     */
    @RequestMapping("/getProductList")
    public List<ProductVo> getProductList() {
        return productService.getProductList();
    }


    /**
     * 修改手机号
     *
     * @param hotelUserAccount
     * @return
     */
    @PostMapping("/updateMobile")
    public CommonResultVo<String> updateMobile(HotelUserAccount hotelUserAccount) {
        return hotelUserAccountService.updateById(hotelUserAccount) ? CommonResultVo.SUCCESS() : CommonResultVo.FAILURE();
    }

    /**
     * 日志分页
     *
     * @param pageIndex
     * @param pageSize
     * @param log
     * @return
     */
    @RequestMapping("/operationLogLimit")
    public PageUtil<OperationLog> operationLogLimit(Integer pageIndex, Integer pageSize, OperationLog log) {
        return operationLogService.operationLogLimit(pageIndex, pageSize, log);
    }

    /**
     * 保存酒店+
     *
     * @param hotel
     * @return
     */
    @PostMapping("/saveHotel")
    public CommonResultVo<String> saveHotel(@Valid Hotel hotel) {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }

        //判断酒店名是否存在
        long count = hotelService.count(new QueryWrapper<>(Hotel.builder()
                .hotelName(hotel.getHotelName())
                .hotelUserId(hotel.getHotelUserId())
                .build()));

        ValidUtils.isFalseThrow(count == 0L, "酒店名已存在");

        hotel.setCreateTime(LocalDateTime.now());

        hotelService.save(hotel);

        HotelUser hotelUser = HotelUser.builder()
                .hotelUserId(hotel.getHotelUserId())
                .hotelId(hotel.getId())
                .memberUserId(hotel.getHotelUserId())
                .isDefault(false)
                .isMain(true)
                .createTime(LocalDateTime.now()).build();

        HotelRole hotelRole = HotelRole.builder()
                .hotelId(hotelUser.getHotelId())
                .roleName("default")
                .remark("default")
                .createUserId(hotel.getHotelUserId())
                .createTime(LocalDateTime.now()).build();

        hotelRoleService.save(hotelRole);

        HotelUserRole hotelUserRole = HotelUserRole.builder()
                .hotelId(hotelRole.getHotelId())
                .hotelUserId(hotelRole.getCreateUserId())
                .roleId(hotelRole.getRoleId()).build();

        hotelUserRoleService.save(hotelUserRole);

        return hotelUserService.save(hotelUser) ? CommonResultVo.SUCCESS() : CommonResultVo.FAILURE();
    }


    /**
     * 获取酒店分页
     *
     * @param pageIndex
     * @param pageSize
     * @param hotel
     * @return
     */
    @GetMapping("/getHotelLimit")
    public PageUtil<Hotel> getHotelLimit(Integer pageIndex, Integer pageSize, Hotel hotel) {
        return hotelUserAccountService.getHotelLimit(pageIndex, pageSize, hotel);
    }


    /**
     * 获取酒店用户分页
     *
     * @param pageIndex
     * @param pageSize
     * @param userAccount
     * @return
     */
    @GetMapping("/getHotelUserLimit")
    public PageUtil<HotelUserAccount> getHotelUserLimit(Integer pageIndex, Integer pageSize, HotelUserAccount userAccount) {
        return hotelUserAccountService.getHotelUserLimit(pageIndex, pageSize, userAccount);
    }


    /**
     * 删除产品升级包
     *
     * @param id
     * @return
     */
    @RequestMapping("/removeProductUpgrade")
    public CommonResultVo<String> removeProductUpgrade(Integer id) {
        ProductUpgrade productUpgrade = productUpgradeService.getById(id);

        ValidUtils.isNullThrow(productUpgrade, "升级包不存在");

        upgradeRecordService.remove(new QueryWrapper<>(UpgradeRecord.builder()
                .hardWareVersion(productUpgrade.getHardWareVersion())
                .softWareVersion(productUpgrade.getNewVersion()).build()));

        return productUpgradeService.removeById(id) ? CommonResultVo.SUCCESS() : CommonResultVo.FAILURE();
    }

    /**
     * 查询酒店设备分页
     *
     * @return
     */
    @RequestMapping("/findHotelDevicePage")
    public PageUtil<UserDevice> findHotelDevicePage(Integer pageIndex, Integer pageSize, String deviceId, String deviceName, String userId) {
        UserDevice userDevice = UserDevice.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .userId(userId).build();
        return userDeviceService.findHotelDevicePage(pageIndex, pageSize, userDevice);
    }

    /**
     * 查询酒店分页
     *
     * @param pageIndex
     * @param pageSize
     * @param hotel
     * @return
     */
    @RequestMapping("/findHotelPage")
    public PageUtil<Hotel> findHotelPage(Integer pageIndex, Integer pageSize, Hotel hotel) {
        return hotelService.findHotelPage(pageIndex, pageSize, hotel);
    }


    /**
     * 删除OTA记录
     *
     * @param deviceId
     * @param status
     * @return
     */
    @RequestMapping("/removeUpgrade")
    public CommonResultVo<String> removeUpgrade(String deviceId, String status) {
        UserDevice userDevice = userDeviceService.getById(deviceId);

        ValidUtils.isNullThrow(userDevice, "设备已解绑或不存在");

        if (status == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }

        upgradeRecordService.removeByMap(new HashMap<>() {
            {
                put("device_id", userDevice.getDeviceId());
            }
        });

        return CommonResultVo.SUCCESS();
    }


    /**
     * 升级OTA
     *
     * @return
     */
    @RequestMapping("/addUpgrade")
    public CommonResultVo<String> addUpgrade(String deviceIds, Integer id, Integer productId, String status) {

        if (UserDto.getUser() == null) {

            if (status == null) {
                ValidUtils.isNullThrow(null, "未登录");
            }
        }
        deviceIds = deviceIds.substring(0, deviceIds.length() - 1);

        ProductUpgrade productUpgrade = productUpgradeService.getById(id);

        ValidUtils.isNullThrow(productUpgrade, "升级包不存在或已删除");

        Product product = productService.getById(productId);

        ValidUtils.isNullThrow(productUpgrade, "产品不存在或已删除");

        // 查询一遍
        for (String deviceId : deviceIds.split(",")
        ) {
            UserDevice userDevice = userDeviceService.getById(deviceId);

            ValidUtils.isNullThrow(userDevice, "设备已解绑或不存在");
        }

        for (String deviceId : deviceIds.split(",")
        ) {
            UserDevice userDevice = userDeviceService.getById(deviceId);


            //升级的时候，选中的版本号标记到硬件版本号上
            userDevice.setSoftWareVersion(productUpgrade.getNewVersion());
            userDevice.setHardWareVersion(productUpgrade.getHardWareVersion());
            Device byId = deviceService.getById(deviceId);

            if (byId == null) {
                continue;
            }

            byId.setVersion(productUpgrade.getNewVersion());
            deviceService.updateById(byId);
            userDeviceService.updateById(userDevice);

            /* todo 设备端要求不下发,下发重启即可 */
            /*MqttOtaDto mqttOtaDto = MqttOtaDto.builder()
                    .filepath(productUpgrade.getVersionUrl())
                    .softwareversion(productUpgrade.getNewVersion())
                    .hardwareversion(productUpgrade.getHardWareVersion())
                    .productId(product.getProductId())
                    .deviceId(deviceId)
                    .build();
            String topic = PubTopicEnum.handlerTopic(PubTopicEnum.PUB_DEVICE_OTA, product.getProductId(), deviceId);
            MQTT.publish(topic, JSON.toJSONString(mqttOtaDto));
            log.info("Mqtt-Send:" + topic + "=" + JSON.toJSONString(mqttOtaDto));*/

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
                    deviceId(byId.getId())
                    .createTime(LocalDateTime.now())
                    .filePath(productUpgrade.getVersionUrl())
                    .softWareVersion(productUpgrade.getNewVersion())
                    .hardWareVersion(productUpgrade.getHardWareVersion())
                    .isSuccess(0).build());
        }
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询设备通过oat
     *
     * @return
     */
    @RequestMapping("/findUserDeviceWithOta")
    public PageUtil<UserDevice> findUserDeviceWithOta(Integer pageIndex, Integer pageSize, String productId, String hardWareVersion, String deviceId) {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }
        UserDevice userDevice = UserDevice.builder()
                .productId(productId)
                .deviceId(deviceId)
                .hardWareVersion(hardWareVersion).build();

        return userDeviceService.findUserDeviceWithOta(pageIndex, pageSize, userDevice);
    }

    /**
     * 新增升级包
     *
     * @param productUpgrade 升级包内容
     * @return
     */
    @RequestMapping("/addProductUpgrade")
    public CommonResultVo<String> addProductUpgrade(ProductUpgrade productUpgrade) {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }
        log.info("productUpgrade={}", productUpgrade);
        productUpgrade.setCreateTime(LocalDateTime.now());

        return productUpgradeService.save(productUpgrade) ? CommonResultVo.SUCCESS() : CommonResultVo.FAILURE();
    }

    /**
     * 升级版本分页
     *
     * @return
     */
    @RequestMapping("/productUpgradePage")
    public PageUtil<ProductUpgrade> productUpgradePage(Integer pageIndex, Integer pageSize, String productId, String hardWareVersion) {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }

        ProductUpgrade productUpgrade = ProductUpgrade.builder()
                .productId(productId).hardWareVersion(hardWareVersion).build();

        return productUpgradeService.productUpgradePage(pageIndex, pageSize, productUpgrade);
    }

    /**
     * 查找主控升级版本
     *
     * @return
     */
    @GetMapping("/findUpgradeGroup")
    public CommonResultVo<List<String>> findUpgradeGroup() {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }
        return CommonResultVo.SUCCESS(productUpgradeService.findUpgradeGroup());
    }

    /**
     * 查找主控版本
     *
     * @return
     */
    @GetMapping("/findMasterProduct")
    public CommonResultVo<List<Product>> findMasterProduct() {
        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }
        return CommonResultVo.SUCCESS(productService.selectProductMaster());
    }

    /**
     * 用户信息
     */
    @GetMapping("/info")
    public CommonResultVo<HotelUserAccount> info() {

        if (UserDto.getUser() == null) {
            ValidUtils.isNullThrow(null, "未登录");
        }

        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount
                .builder()
                .actualUserId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(user, "数据不存在");
        return CommonResultVo.SUCCESS(user);
    }


    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    public CommonResultVo<LoginVo<HotelUserAccount>> login(@RequestBody @Valid AdminLoginDto loginDto) {

        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount.builder()
                .mobile(loginDto.getAccount())
                .password(MD5Utils.md5(loginDto.getPwd()))
                .type("-1")
                .build()));

        log.info("MD5={}", MD5Utils.md5(loginDto.getPwd()));
        ValidUtils.isNullThrow(user, "账号或密码输入错误");

        //登录token 缓存token
        String token = LoginUtils.login(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .actualUserId(user.getActualUserId())
                .build());

        return CommonResultVo.SUCCESS(LoginVo.<HotelUserAccount>builder()
                .account(user.getMobile())
                .userInfo(user)
                .token(token).build());
    }
}

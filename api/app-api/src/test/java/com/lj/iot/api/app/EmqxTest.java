package com.lj.iot.api.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.base.vo.ExportDeviceJsonVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.mapper.UserDeviceMapper;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizIrDataService;
import com.lj.iot.biz.service.BizIrDeviceService;
import com.lj.iot.biz.service.mqtt.push.service.MqttPushService;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class EmqxTest {

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private JDatService jDatService;

    @Autowired
    private IProductTypeService productTypeService;

    @Autowired
    private IProductService productService;

    @Resource
    MqttPushService mqttPushService;

    @Autowired
    private BizIrDeviceService bizIrDataService;

    @Resource
    private UserDeviceMapper userDeviceMapper;



    @Test
    public void updateDeviceStatus() {
        List<String> list = userDeviceService.editDeviceContextStatus();

        for (String deviceId : list
        ) {
            userDeviceService.update(
                    UserDevice.builder()
                            .status(true)
                            .statusTime(LocalDateTime.now())
                            .build(),
                    new QueryWrapper<>(UserDevice.builder()
                            .deviceId(deviceId)
                            .build()));
        }
    }


    @Test
    public void checkDeviceStatus() throws InterruptedException {

        /*JDat upJdat = jDatService.getOne(new QueryWrapper<>(JDat.builder()
                .tags("70000000_6").build()));


        JDat downJdat = jDatService.getOne(new QueryWrapper<>(JDat.builder()
                .tags("70000000_7").build()));

        log.info("上={}",upJdat.getDats());

        log.info("下={}",downJdat.getDats());*/

        UserDevice masterUserDevice = userDeviceService.getById("144b93a2422f");

        Product product = productService.getById(masterUserDevice.getProductId());

        //获取红外码
        String irData = jDatService.getOne(new QueryWrapper<>(JDat.builder()
                .tags("70000000_7").build())).getDats();

        ValidUtils.isNullThrow(irData, "当前遥控器无此按键");
        //发送红外数据
        ProductType topProductType = productTypeService.getTopProductType(product.getProductType());

        JSONObject extend = bizIrDataService.extendData(topProductType.getProductType(), masterUserDevice.getThingModel());

        for (int i = 0; i < 100; i++) {
            mqttPushService.pushFROrIRCode(masterUserDevice, SignalEnum.IR, irData.split(","), extend);
            Thread.sleep(350);
        }

    }


    @Test
    public void getOlneDevice() {
        try {
            String username = "app_api_test";
            String password = "123456";

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://47.100.238.205:18083/api/v5/clients/tangyuanjie4")
                    .header("Content-Type", "application/json")
                    .header("Authorization", Credentials.basic(username, password))
                    .build();

            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void importEmqxByBatchCode() throws IOException {
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .productId("213350486").build()));

        List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                        .user_id(device.getId())
                        .password_hash(device.getCCCFDF())
                        .build())
                .collect(Collectors.toList());

        //创建json文件*/
        String path = "C:\\Users\\A\\Desktop\\inemqx.json";
        createTxt(JSON.toJSONString(deviceJsonVoList), path);

        log.info("deviceJsonVoList.size={}", deviceJsonVoList.size());
        import_users(path);
    }

    @Test
    public void importEmqx() throws IOException {

        List<Device> deviceList = deviceService.list();

        List<ExportDeviceJsonVo> deviceJsonVoList = deviceList.stream().map(device -> ExportDeviceJsonVo.builder()
                        .user_id(device.getId())
                        .password_hash(device.getCCCFDF())
                        .build())
                .collect(Collectors.toList());
        //创建json文件*/
        String path = "C:\\Users\\A\\Desktop\\inemqx.json";
        createTxt(JSON.toJSONString(deviceJsonVoList), path);
        //上传给emqx
        import_users(path);
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
                .url("http://47.100.238.205:18083/api/v5/authentication/password_based%3Abuilt_in_database/import_users")
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
    }
}

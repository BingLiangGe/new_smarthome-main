package com.lj.iot.biz.db.smart.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.tea.okhttp.OkRequestBody;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.OtaDeviceIdPageDto;
import com.lj.iot.biz.base.vo.ActivationVo;
import com.lj.iot.biz.base.vo.DevicePageVo;
import com.lj.iot.biz.base.vo.DeviceStatisticsVo;
import com.lj.iot.biz.db.smart.entity.Device;
import com.lj.iot.biz.db.smart.mapper.DeviceMapper;
import com.lj.iot.biz.db.smart.service.IDeviceService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;

/**
 * 设备出厂表 服务实现类
 *
 * @author xm
 * @since 2022-07-20
 */
@DS("smart")
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

    @Resource
    private DeviceMapper mapper;

    @Value("${mqtt.client.host}")
    private String hostIp;

    @Override
    public CommonResultVo<ActivationVo> activation3326(Map<String, Object> params) throws IOException {

        String androidId = String.valueOf(params.get("androidId"));

        Device device = Device.builder()
                .id(DeviceIdUtils.hexId())
                .productId("1000000100")
                .CCCFDF(IdUtils.uuid())
                .androidId(androidId)
                .batchCode("activation3326")
                .build();

        Device deviceDB = getOne(new QueryWrapper<>(Device.builder()
                .androidId(androidId).build()));

        if (deviceDB != null){
            return CommonResultVo.SUCCESS(ActivationVo.builder()
                    .deviceId(deviceDB.getId())
                    .CCCFDF(deviceDB.getCCCFDF())
                    .productId(deviceDB.getProductId()).build());
        }

        save(device);

        JSONObject paramJson = new JSONObject();
        paramJson.put("password", device.getCCCFDF());
        paramJson.put("user_id", device.getId());

        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, paramJson.toJSONString());

        String credential = null;
        if ("47.100.211.192".equals(hostIp) || "emqxprod.lj-smarthome.com".equals(hostIp)) {
            credential = Credentials.basic("admin", "pulicadmin");
        } else {
            credential = Credentials.basic("admin", "public");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("http://" + hostIp + ":18083/api/v5/authentication/password_based:built_in_database/users")
                .method("POST", requestBody)
                .header("Authorization", credential)
                .build();

        Response response = client.newCall(request).execute();

        ValidUtils.isFalseThrow(response.isSuccessful(), "上传mq数据异常,请联系管理员");

        return CommonResultVo.SUCCESS(ActivationVo.builder()
                .deviceId(device.getId())
                .CCCFDF(device.getCCCFDF())
                .productId(device.getProductId()).build());
    }

    @Override
    public List<String> findNotBindDevice() {
        return mapper.findNotBindDevice();
    }

    @Override
    public List<String> findUserAcccountDeviceNotHotel(Integer productId) {
        return mapper.findUserAcccountDeviceNotHotel(productId);
    }

    @Override
    public IPage<DevicePageVo> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public IPage<DevicePageVo> NewCustomPage(OtaDeviceIdPageDto pageDto) {
        IPage<DevicePageVo> page = PageUtil.page(pageDto);
        return this.baseMapper.NewCustomPage(page, pageDto);
    }

    @Override
    public DeviceStatisticsVo statistics(PageDto pageDto) {
        return DeviceStatisticsVo.builder()
                .activation(this.baseMapper.activation(pageDto))
                .unActivation(this.baseMapper.unActivation(pageDto))
                .build();
    }

    @Override
    public DeviceStatisticsVo newStatistics(OtaDeviceIdPageDto pageDto) {
        return DeviceStatisticsVo.builder()
                .activation(this.baseMapper.newActivation(pageDto))
                .unActivation(this.baseMapper.newUnActivation(pageDto))
                .build();
    }

    @Override
    public String sha256(String deviceId) {
        Device device = this.getById(deviceId);
        ValidUtils.isNullThrow(device, "设备不存在");

        //产品ID 8位16进制   不够8位前面补零
        String productIdHex = String.format("%08x", Long.parseLong(device.getProductId()));
        return SecureUtil.sha256(productIdHex + "," + device.getId() + "," + device.getCCCFDF()).substring(0, 32);
    }

    public static void main(String[] args) {
        String productIdHex = String.format("%08x", Long.parseLong("10124504"));
        System.out.println(productIdHex);
        String a = productIdHex + "," + "122105fe2b00" + "," + "57829601f4934b1190b39060e33904bc";
        System.out.println(a);
        String hash = SecureUtil.sha256(a);
        System.out.println(hash);
    }
}
package com.lj.iot.api.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.app.web.open.OpenUserController;
import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.base.dto.LoginSmsDto;
import com.lj.iot.biz.base.dto.UserDeviceAddDto;
import com.lj.iot.biz.base.vo.MasterDeviceDto;
import com.lj.iot.biz.base.vo.MqttResultVo;
import com.lj.iot.biz.db.smart.entity.*;
import com.lj.iot.biz.db.smart.service.*;
import com.lj.iot.biz.service.BizDeviceService;
import com.lj.iot.biz.service.BizIrDataService;
import com.lj.iot.biz.service.BizMusicOrderService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.aiui.core.dto.UploadEntityDto;
import com.lj.iot.common.aiui.core.dto.UploadEntityItemDto;
import com.lj.iot.common.aiui.core.service.IUploadEntityService;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.mqtt.client.core.HandleMessage;
import com.lj.iot.common.mqtt.client.core.MQTT;
import com.lj.iot.common.util.IdUtils;
import com.lj.iot.common.util.MD5Utils;
import com.lj.iot.common.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@SpringBootTest
class AppApiApplicationTests {

    @Autowired
    private BizIrDataService irDataService;

    @Autowired
    private JDatService jDatService;

    @Autowired
    private IIrModelService modelService;

    @Test
    public void getDjiAirContr(){
        List<IrModel> list= irDataService.getDjAirContr();
        List<JDat> jdatList= new ArrayList<>();


        for (int i=0;i<list.size();i++){
          IrModel model=list.get(i);
            IrModel DjModel=new IrModel();

            DjModel.setBrandId(4009L);
            DjModel.setDeviceTypeId(1L);
            DjModel.setBrandName(model.getBrandName());
            DjModel.setModeName(model.getModeName());
            DjModel.setFileType(model.getFileType());
            DjModel.setFileId("999999"+(i+1));

            modelService.save(DjModel);

           /* List<IrData> irDataList= irDataService.getDjAirContData(model.getFileId());

            log.info("model={}",DjModel);
            for (IrData irData:irDataList
                 ) {
                JDat jDat=new JDat();
                jDat.setTags(DjModel.getFileId()+"_"+irData.getDataIndex());
                jDat.setDats(irData.getIrData());
                log.info("      mode_size={}",jDat);
                jdatList.add(jDat);
            }
            log.info("");*/
        }

        log.info("jDataSize={}",jdatList.size());
        //jDatService.saveBatch(jdatList);
        /*List<IrData> irDataList= irDataService.getDjAirContData(list);

        List<JDat> jdatList= new ArrayList<>();

        irDataList.forEach(model ->{
            JDat jDat=new JDat();
            jDat.setTags(model.getFileId()+"_"+model.getDataIndex());
            jDat.setDats(model.getIrData());
            log.info("mode_size={},dataIndex={}",irDataList.size(),jDat.getTags());
            jdatList.add(jDat);
        });
        jDatService.saveBatch(jdatList);*/
    }


    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(HomeRoom.builder().homeId(83283L).build()));
        List<Long> deviceIds = new ArrayList<>();
        deviceIds.add(4L);
        deviceIds.add(5L);
        deviceIds.add(6L);
        deviceIds.add(7L);

        String ids = "" + deviceIds.get(0);
        for (int i = 1; i < deviceIds.size(); i++) {
            ids = ids + "," + deviceIds.get(i);
        }
        System.out.println(ids);
        String[] pwda = {"0x88", "0x9E", "0xA3", "0x31", "0x72", "0xEE", "0xDC", "0x50", "0x0E", "0xA7", "0x94", "0xEB", "0x34", "0x7A", "0xB9", "0x12",
                "0x8F", "0xAB", "0x87", "0xCE", "0x20", "0xC2", "0x7E", "0x85", "0x11", "0x6B", "0xC7", "0x62", "0x2F", "0x73", "0x06", "0xDD",
                "0x69", "0x6A", "0x4D", "0x84", "0xE3", "0x29", "0x17", "0x25", "0x10", "0x0F", "0x08", "0x3E", "0x4F", "0xE9", "0x36", "0x38",
                "0x0D", "0x82", "0xA6", "0xB4", "0x7F", "0x04", "0x63", "0xC9", "0x22", "0x66", "0x6F", "0x86", "0x3F", "0x59", "0xDE", "0x47",
                "0x77", "0xDB", "0xC6", "0x58", "0x7B", "0x8C", "0x9A", "0xC5", "0x83", "0x23", "0xD4", "0xB1", "0xA8", "0xAF", "0xFA", "0xAE",
                "0x43", "0x1E", "0xF0", "0xEF", "0xC0", "0x26", "0x07", "0x79", "0x75", "0x2A", "0xBD", "0x27", "0xD7", "0xDA", "0xAC", "0xAA",
                "0xE1", "0xE6", "0xDF", "0x89", "0xC4", "0x92", "0x6E", "0xE8", "0xF9", "0x56", "0xB3", "0xA2", "0x44", "0xD5", "0xCC", "0x1D",
                "0x71", "0xCD", "0x45", "0x70", "0x51", "0x68", "0xFB", "0x4B", "0x97", "0x65", "0x2C", "0x6C", "0x0A", "0xC3", "0xFC", "0xD3",
                "0x09", "0x46", "0x95", "0xED", "0xE0", "0x8A", "0xB6", "0x2E", "0xE7", "0x7D", "0x42", "0xCA", "0x53", "0x5C", "0x4C", "0x93",
                "0xD8", "0x49", "0x96", "0xF2", "0xD6", "0x6D", "0x1C", "0x14", "0x91", "0x35", "0x3D", "0xD9", "0x21", "0xE4", "0x8B", "0x18",
                "0x40", "0x98", "0x37", "0xFD", "0x9B", "0xF7", "0xA1", "0x2D", "0xBC", "0x55", "0x4A", "0x33", "0x9D", "0x24", "0xF6", "0xB7",
                "0x39", "0x99", "0x8E", "0x5E", "0xBF", "0x41", "0xFF", "0x19", "0x02", "0xCF", "0xBA", "0x3C", "0x28", "0xE5", "0x15", "0x5D",
                "0x30", "0x5B", "0xD2", "0xEA", "0xA9", "0x81", "0xB8", "0x61", "0x05", "0x2B", "0x13", "0xD0", "0xAD", "0x3B", "0xF4", "0x01",
                "0x0B", "0xA0", "0x90", "0xFE", "0x1B", "0x64", "0x1A", "0x16", "0xC1", "0x67", "0x5F", "0xF1", "0xF8", "0x48", "0x3A", "0x60",
                "0x54", "0xB0", "0xCB", "0x8D", "0x9C", "0xA4", "0xB2", "0x0C", "0xF5", "0xF3", "0x00", "0x32", "0xC8", "0x57", "0xEC", "0x74",
                "0x4E", "0x7C", "0x76", "0xBE", "0x80", "0xA5", "0xB5", "0xE2", "0x9F", "0x1F", "0xD1", "0x5A", "0x52", "0xBB", "0x03", "0x78"};
        int[] a = new int[pwda.length];
        for (int i = 0; i < pwda.length; i++) {
            a[i] = Integer.parseInt(pwda[i].substring(2), 16);
        }
        System.out.println(Arrays.toString(a));
        String[] pwdb = {"0x89", "0x9A", "0xB0", "0x09", "0xE9", "0x84", "0x26", "0x7E", "0x7C", "0x55", "0x92", "0xF3", "0x1E", "0xBA", "0xEC", "0x6C",
                "0x72", "0x11", "0x76", "0x14", "0x73", "0xB7", "0xA0", "0x1F", "0xF5", "0x12", "0xA8", "0xBE", "0xE2", "0x21", "0xEF", "0xA5",
                "0x33", "0x2E", "0xFF", "0x29", "0x38", "0x70", "0x8A", "0xF4", "0xB9", "0xE1", "0x82", "0xE4", "0xDB", "0xEB", "0x1D", "0x52",
                "0xFD", "0x98", "0x64", "0xAD", "0x93", "0x36", "0x4E", "0x3E", "0x5B", "0x3D", "0x04", "0xA4", "0xA9", "0x17", "0x1B", "0xA7",
                "0xD8", "0x78", "0x71", "0x99", "0x19", "0x01", "0x6F", "0x8B", "0x79", "0xA3", "0x77", "0x24", "0xF2", "0x85", "0xC6", "0x31",
                "0x9B", "0xC3", "0x8D", "0x37", "0xA6", "0xD2", "0x35", "0x83", "0xFE", "0x6D", "0xFA", "0x4A", "0x62", "0xF1", "0x69", "0x03",
                "0x6A", "0x0F", "0x42", "0x97", "0x43", "0x4B", "0x53", "0x91", "0xCB", "0x5E", "0xD7", "0x68", "0x34", "0x8E", "0x7D", "0xCD",
                "0x57", "0x95", "0x39", "0x2A", "0x49", "0x06", "0x1A", "0x63", "0xE5", "0x0D", "0x66", "0x5F", "0x32", "0xB4", "0x54", "0xD3",
                "0x44", "0x65", "0x9C", "0xD9", "0x9F", "0xD1", "0x60", "0x05", "0x3B", "0x13", "0xC5", "0x46", "0x41", "0xF6", "0xC0", "0xBD",
                "0xCA", "0x08", "0xFC", "0x9D", "0x0A", "0xD0", "0xBF", "0x6B", "0x67", "0x27", "0xAC", "0x61", "0x9E", "0x59", "0x7B", "0x0B",
                "0x5A", "0xC7", "0x15", "0x86", "0xEE", "0x48", "0x1C", "0xE3", "0x47", "0xEA", "0x8F", "0x2F", "0xC8", "0x2B", "0x87", "0xC1",
                "0x22", "0xF7", "0x5D", "0x07", "0x56", "0xC4", "0xF8", "0xE0", "0x10", "0xAF", "0x8C", "0xCF", "0x00", "0x02", "0xF9", "0x16",
                "0xD6", "0x23", "0x4F", "0x58", "0x75", "0x7F", "0x0C", "0xD4", "0x2D", "0xE7", "0xE8", "0x0E", "0x7A", "0xAE", "0xB8", "0x88",
                "0x30", "0x90", "0x45", "0xB2", "0x40", "0xE6", "0x74", "0xAA", "0xB3", "0x5C", "0xED", "0xBB", "0xDE", "0x4C", "0xB5", "0x3A",
                "0x51", "0x3C", "0xC2", "0x50", "0xB6", "0xA2", "0x3F", "0xF0", "0x2C", "0xDC", "0xDA", "0x94", "0xDF", "0xAB", "0x28", "0x81",
                "0xBC", "0xFB", "0x25", "0xB1", "0xCC", "0xA1", "0xD5", "0x18", "0x80", "0x96", "0xC9", "0xCE", "0x20", "0x6E", "0x4D", "0xDD"};
        int[] b = new int[pwdb.length];
        for (int i = 0; i < pwdb.length; i++) {
            b[i] = Integer.parseInt(pwdb[i].substring(2), 16);
        }
        System.out.println(Arrays.toString(b));
        String[] pwdc = {"0x80", "0x54", "0xA3", "0xCE", "0xB2", "0xCB", "0x6B", "0x4A", "0x3E", "0xB1", "0x96", "0x8C", "0x7A", "0x08", "0x99", "0x18",
                "0x0E", "0x33", "0x30", "0x8D", "0x15", "0x13", "0x51", "0xB4", "0x3B", "0x7E", "0xAD", "0x59", "0x65", "0x60", "0x4E", "0x6E",
                "0xF2", "0x3A", "0xF7", "0xCC", "0x27", "0x52", "0x26", "0xF3", "0xD8", "0x35", "0x24", "0xEB", "0xDF", "0x90", "0x86", "0x9D",
                "0x38", "0xF9", "0x31", "0x0D", "0x5F", "0x87", "0x3F", "0xE8", "0xD1", "0xE9", "0x8E", "0xA6", "0xBA", "0xEA", "0xC8", "0x36",
                "0xA7", "0xC1", "0x6D", "0x5E", "0x82", "0xEC", "0x4C", "0xB0", "0xDB", "0xA8", "0x00", "0x17", "0x4B", "0x32", "0x37", "0xA2",
                "0x91", "0xA0", "0x34", "0x58", "0x92", "0x03", "0xE6", "0x8B", "0xFA", "0xE0", "0x5D", "0xC4", "0xDA", "0x95", "0xEE", "0x45",
                "0xD0", "0xDD", "0x70", "0xFC", "0xB8", "0x0C", "0xCA", "0xCF", "0xBF", "0x3D", "0x5A", "0xC3", "0xC0", "0xF8", "0x76", "0xAB",
                "0xFB", "0x40", "0x44", "0xB6", "0x71", "0xD7", "0xE7", "0x3C", "0x8F", "0x79", "0x84", "0xC7", "0x66", "0x22", "0x20", "0x41",
                "0x25", "0x9C", "0x2F", "0x74", "0xC9", "0x9F", "0x29", "0x1E", "0x7C", "0xED", "0x55", "0x63", "0xDE", "0x2C", "0x16", "0x19",
                "0x6F", "0x05", "0xE3", "0x6C", "0xA1", "0x0B", "0x4F", "0x53", "0x2E", "0x09", "0x46", "0xC2", "0x85", "0x62", "0xF4", "0x88",
                "0x9B", "0x69", "0xB9", "0x1A", "0x68", "0x81", "0xBE", "0xD2", "0xB3", "0x93", "0xF0", "0x39", "0x4D", "0x5C", "0x1C", "0x67",
                "0x1B", "0x56", "0x47", "0x7D", "0x61", "0x97", "0x49", "0x7B", "0xC5", "0xCD", "0xAE", "0x06", "0xFE", "0x2D", "0xA4", "0x50",
                "0x94", "0x8A", "0x73", "0x04", "0xA9", "0x64", "0x98", "0xD4", "0xAA", "0xB5", "0x43", "0x57", "0x1D", "0xF1", "0x78", "0xC6",
                "0xE5", "0x89", "0xE2", "0x0F", "0x07", "0x7F", "0x83", "0xAC", "0x42", "0xFD", "0x0A", "0x28", "0xAF", "0xBD", "0x12", "0xDC",
                "0x23", "0x2B", "0xF6", "0xEF", "0xD5", "0xB7", "0x9A", "0xD6", "0x75", "0xF5", "0x72", "0x21", "0xA5", "0x1F", "0x6A", "0x5B",
                "0x2A", "0xE4", "0x01", "0x14", "0x9E", "0x48", "0x77", "0xBB", "0x10", "0xFF", "0xE1", "0xD9", "0xD3", "0x02", "0xBC", "0x11"};
        int[] c = new int[pwdc.length];
        for (int i = 0; i < pwdc.length; i++) {
            c[i] = Integer.parseInt(pwdc[i].substring(2), 16);
        }
        System.out.println(Arrays.toString(c));
    }

    private static void convRemotcode(String[] pwda, String[] pwdb, String[] pwdc) {

        String[] srcData = {"1", "2"};

        int[] dstData = new int[srcData.length];
        int K1, K2, K3, pv;
        int scnt = 0;
        K1 = Integer.parseInt(srcData[0]);
        K2 = srcData.length - 1;
        dstData[0] = K2;
        for (int i = 1; i < srcData.length; i++) {
            pv = (Integer.parseInt(pwda[i]) + Integer.parseInt(pwdb[K2]) - (Integer.parseInt(pwdc[K1]))) % 256;
            dstData[i] = Integer.parseInt(srcData[i]) ^ pv;
        }

        if (dstData[3] > 0) {
            scnt++;
            if (scnt % 2 > 0) {
                if (dstData[3] != 0xff) {
                    dstData[dstData[3]] = dstData[dstData[3]] ^ dstData[4];
                } else {
                    K1 = dstData[26];
                    K2 = dstData[27];
                    K3 = dstData[28];
                    dstData[26] = dstData[29];
                    dstData[27] = dstData[30];
                    dstData[28] = dstData[31];
                    dstData[29] = dstData[32];
                    dstData[30] = K1;
                    dstData[31] = K2;
                    dstData[32] = K3;
                }
            }
        }
    }

    @Autowired
    private BizMusicOrderService orderService;

    @Autowired
    private IUploadEntityService uploadEntityService;
    @Autowired
    private BizDeviceService bizDeviceService;

    @Autowired
    private IInfraredTestService infraredTestService;

    @Test
    public void mustic(){
        String appId = "88ffd758";
        String appKey = "a82f8e9716f060c533f5c3ecd1d24476";

        Long timestamp = new Date().getTime();
        Map map = new HashMap<String, String>();
        map.put("appId", appId);
        map.put("timestamp", timestamp + "");
        map.put("token", MD5Utils.standardSign(appId + appKey + timestamp));
        map.put("userId", "20230601140939716654633157701632");
        map.put("serialNumber", "13e1da5adb03");
        map.put("deviceModel", "MASTER");

        try {
            String result = OkHttpUtils.post("https://adf.xfyun.cn/kuwo/active", map);
            log.info("BizMusicOrderServiceImpl.active" + result);
            JSONObject jsonObject = JSON.parseObject(result);
            if (!jsonObject.getInteger("code").equals(200)) {
                throw CommonException.FAILURE(jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            log.error("BizMusicOrderServiceImpl.active", e);
        }
    }


    /**
     * 测试红外
     */
    @Test
    public void testInfrared() throws InterruptedException {
        log.info("testInfrared--------------------->");

        int pageSize = 10000;
        int pageIndex = 3230001;

        List<JDat> dataList = jDatService.page(pageIndex,pageSize);

        while (dataList.size() > 0) {

            for (JDat jDat : dataList
            ) {

                try {
                    // 存在对应码
                    if (jDat != null) {
                        String data = null;

                        String fileId=jDat.getTags().split("_")[0];
                        // 自学码
                        if (checkSelfStudyCode(fileId)) {
                            data = jDat.getDats();
                        } else {
                            // 解析json 获取irdata
                            JSONObject irJson = JSONObject.parseObject(jDat.getDats());
                            data = decode(irJson.getString("irdata"));
                        }
                        //log.info("------success------>data={}",data);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.error("出现错误，insertDB={}",jDat);
                    infraredTestService.save(InfraredTest.builder().tags(jDat.getTags())
                                    .brandName(e.getMessage())
                                    .commd(jDat.getDats())
                                    .createTime(LocalDateTime.now())
                            .build());
                    log.info("");
                }
            }

            pageIndex += pageSize;
            dataList = jDatService.page(pageIndex,pageSize);

            log.info("-----------------------sleep={}-------------------------------",pageIndex);
            Thread.sleep(1500);
            log.info("");
            log.info("");
            log.info("");
        }
    }

    /**
     * 验证是否为自学码
     *
     * @param fileId
     * @return
     */
    public boolean checkSelfStudyCode(String fileId) {

        String[] saleStudys = {"999991", "999992", "999998", "999981", "999980", "999970","999999"};

        for (String code : saleStudys
        ) {
            // 为自学码
            if (code.equals(fileId)) {
                return true;
            }
        }

        return false;
    }
    public static String decode(String srcDataHis) {
        int[] pwda = {136, 158, 163, 49, 114, 238, 220, 80, 14, 167, 148, 235, 52, 122, 185, 18, 143, 171, 135, 206, 32, 194, 126, 133, 17, 107, 199, 98, 47, 115, 6, 221, 105, 106, 77, 132, 227, 41, 23, 37, 16, 15, 8, 62, 79, 233, 54, 56, 13, 130, 166, 180, 127, 4, 99, 201, 34, 102, 111, 134, 63, 89, 222, 71, 119, 219, 198, 88, 123, 140, 154, 197, 131, 35, 212, 177, 168, 175, 250, 174, 67, 30, 240, 239, 192, 38, 7, 121, 117, 42, 189, 39, 215, 218, 172, 170, 225, 230, 223, 137, 196, 146, 110, 232, 249, 86, 179, 162, 68, 213, 204, 29, 113, 205, 69, 112, 81, 104, 251, 75, 151, 101, 44, 108, 10, 195, 252, 211, 9, 70, 149, 237, 224, 138, 182, 46, 231, 125, 66, 202, 83, 92, 76, 147, 216, 73, 150, 242, 214, 109, 28, 20, 145, 53, 61, 217, 33, 228, 139, 24, 64, 152, 55, 253, 155, 247, 161, 45, 188, 85, 74, 51, 157, 36, 246, 183, 57, 153, 142, 94, 191, 65, 255, 25, 2, 207, 186, 60, 40, 229, 21, 93, 48, 91, 210, 234, 169, 129, 184, 97, 5, 43, 19, 208, 173, 59, 244, 1, 11, 160, 144, 254, 27, 100, 26, 22, 193, 103, 95, 241, 248, 72, 58, 96, 84, 176, 203, 141, 156, 164, 178, 12, 245, 243, 0, 50, 200, 87, 236, 116, 78, 124, 118, 190, 128, 165, 181, 226, 159, 31, 209, 90, 82, 187, 3, 120};

        int[] pwdb = {137, 154, 176, 9, 233, 132, 38, 126, 124, 85, 146, 243, 30, 186, 236, 108, 114, 17, 118, 20, 115, 183, 160, 31, 245, 18, 168, 190, 226, 33, 239, 165, 51, 46, 255, 41, 56, 112, 138, 244, 185, 225, 130, 228, 219, 235, 29, 82, 253, 152, 100, 173, 147, 54, 78, 62, 91, 61, 4, 164, 169, 23, 27, 167, 216, 120, 113, 153, 25, 1, 111, 139, 121, 163, 119, 36, 242, 133, 198, 49, 155, 195, 141, 55, 166, 210, 53, 131, 254, 109, 250, 74, 98, 241, 105, 3, 106, 15, 66, 151, 67, 75, 83, 145, 203, 94, 215, 104, 52, 142, 125, 205, 87, 149, 57, 42, 73, 6, 26, 99, 229, 13, 102, 95, 50, 180, 84, 211, 68, 101, 156, 217, 159, 209, 96, 5, 59, 19, 197, 70, 65, 246, 192, 189, 202, 8, 252, 157, 10, 208, 191, 107, 103, 39, 172, 97, 158, 89, 123, 11, 90, 199, 21, 134, 238, 72, 28, 227, 71, 234, 143, 47, 200, 43, 135, 193, 34, 247, 93, 7, 86, 196, 248, 224, 16, 175, 140, 207, 0, 2, 249, 22, 214, 35, 79, 88, 117, 127, 12, 212, 45, 231, 232, 14, 122, 174, 184, 136, 48, 144, 69, 178, 64, 230, 116, 170, 179, 92, 237, 187, 222, 76, 181, 58, 81, 60, 194, 80, 182, 162, 63, 240, 44, 220, 218, 148, 223, 171, 40, 129, 188, 251, 37, 177, 204, 161, 213, 24, 128, 150, 201, 206, 32, 110, 77, 221};

        int[] pwdc = {128, 84, 163, 206, 178, 203, 107, 74, 62, 177, 150, 140, 122, 8, 153, 24, 14, 51, 48, 141, 21, 19, 81, 180, 59, 126, 173, 89, 101, 96, 78, 110, 242, 58, 247, 204, 39, 82, 38, 243, 216, 53, 36, 235, 223, 144, 134, 157, 56, 249, 49, 13, 95, 135, 63, 232, 209, 233, 142, 166, 186, 234, 200, 54, 167, 193, 109, 94, 130, 236, 76, 176, 219, 168, 0, 23, 75, 50, 55, 162, 145, 160, 52, 88, 146, 3, 230, 139, 250, 224, 93, 196, 218, 149, 238, 69, 208, 221, 112, 252, 184, 12, 202, 207, 191, 61, 90, 195, 192, 248, 118, 171, 251, 64, 68, 182, 113, 215, 231, 60, 143, 121, 132, 199, 102, 34, 32, 65, 37, 156, 47, 116, 201, 159, 41, 30, 124, 237, 85, 99, 222, 44, 22, 25, 111, 5, 227, 108, 161, 11, 79, 83, 46, 9, 70, 194, 133, 98, 244, 136, 155, 105, 185, 26, 104, 129, 190, 210, 179, 147, 240, 57, 77, 92, 28, 103, 27, 86, 71, 125, 97, 151, 73, 123, 197, 205, 174, 6, 254, 45, 164, 80, 148, 138, 115, 4, 169, 100, 152, 212, 170, 181, 67, 87, 29, 241, 120, 198, 229, 137, 226, 15, 7, 127, 131, 172, 66, 253, 10, 40, 175, 189, 18, 220, 35, 43, 246, 239, 213, 183, 154, 214, 117, 245, 114, 33, 165, 31, 106, 91, 42, 228, 1, 20, 158, 72, 119, 187, 16, 255, 225, 217, 211, 2, 188, 17};
        String[] split = srcDataHis.split(",");
        int[] srcData = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            srcData[i] = Integer.parseInt(split[i], 16);
        }
        int[] dstData = new int[srcData.length];
        int K1, K2, K3, pv;
        int scnt = 0;
        K1 = srcData[0];
        K2 = srcData.length - 1;
        dstData[0] = K2;
        for (int i = 1; i < srcData.length; i++) {
            pv = (pwda[i] + pwdb[K2] - pwdc[K1]) % 256;
            dstData[i] = srcData[i] ^ pv;
        }
        if (dstData[3] > 0) {
            scnt++;
            if (scnt % 2 > 0) {
                if ("ff".equalsIgnoreCase(byteToHex(dstData[3]))) {
                    dstData[dstData[3]] = dstData[dstData[3]] ^ dstData[4];
                } else {
                    K1 = dstData[26];
                    K2 = dstData[27];
                    K3 = dstData[28];
                    dstData[26] = dstData[29];
                    dstData[27] = dstData[30];
                    dstData[28] = dstData[31];
                    dstData[29] = dstData[32];
                    dstData[30] = K1;
                    dstData[31] = K2;
                    dstData[32] = K3;
                }
            }
        }
        String[] result = new String[dstData.length];
        for (int i = 0; i < dstData.length; i++) {
            result[i] = byteToHex(dstData[i]);
        }
        return String.join(",", result);
    }

    /**
     * 将一个整形化为十六进制，并以字符串的形式返回
     */
    private final static String[] hexArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    public static String byteToHex(int n) {
        if (n < 0) {
            n = n + 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexArray[d1] + hexArray[d2];
    }

    @Test
    void contextLoads() {
        // orderService.active("998ca00a882c0b001","20221014144259633313811457159168");
        // System.out.println(null=="ok");
//        uploadEntityService.check("psn37e4f349@dx000116bc15dd782d01");
        Device device = bizDeviceService.findById("7089f50006e1");
        device.setVersion("1.0.113");
        bizDeviceService.upDataById(device);

    }

    @Test
    void uploadUserLevel() {
        // orderService.active("998ca00a882c0b001","20221014144259633313811457159168");
        // System.out.println(null=="ok");

        List<UploadEntityItemDto> entityList = new ArrayList<>();
        entityList.add(UploadEntityItemDto.builder().alias("大白").did("1122334455").device("light").build());

        UploadEntityDto uploadEntityDto = new UploadEntityDto();
        uploadEntityDto.setResName("IFLYTEK.smartH_deviceAlias");
        uploadEntityDto.setEntityList(entityList);
        uploadEntityDto.setUserId("20221014144259633313811457159168");
        uploadEntityService.uploadUserLevel(uploadEntityDto);
    }


    @Test
    void uploadCustomUserLevel() {
        // orderService.active("998ca00a882c0b001","20221014144259633313811457159168");
        // System.out.println(null=="ok");

        List<UploadEntityItemDto> entityList = new ArrayList<>();
        entityList.add(UploadEntityItemDto.builder().alias("大白").name("dn").build());

        UploadEntityDto uploadEntityDto = new UploadEntityDto();
        uploadEntityDto.setDynamicEntitiesName("dn");
        uploadEntityDto.setEntityList(entityList);
        uploadEntityDto.setUserId("20221014144259633313811457159168");
        uploadEntityService.uploadUserLevel(uploadEntityDto);
    }

    @Test
    void uploadCustomLevel() {
        // orderService.active("998ca00a882c0b001","20221014144259633313811457159168");
        // System.out.println(null=="ok");

        List<UploadEntityItemDto> entityList = new ArrayList<>();
        entityList.add(UploadEntityItemDto.builder().alias("小黑").name("dn").build());

        UploadEntityDto uploadEntityDto = new UploadEntityDto();
        uploadEntityDto.setDynamicEntitiesName("dn");
        uploadEntityDto.setEntityList(entityList);
        uploadEntityDto.setUserId("20221014144259633313811457159168");
        uploadEntityService.uploadCustomLevel(uploadEntityDto);
    }

    @Autowired
    private BizMusicOrderService bizMusicOrderService;

    @Test
    void active() {
        //bizMusicOrderService.active("8f3248c02c0b003", "20221115135906644899181533638656");
        bizMusicOrderService.active("7089f5000251", "20230112104418665868653421441024");
    }

    @Autowired
    private OpenUserController openUserController;

    @Test
    void createUser() {

        BigDecimal phone = new BigDecimal("13110000000");

        for (int i = 0; i < 1000; i++) {

            phone = phone.add(BigDecimal.ONE);

            LoginDto loginDto = LoginDto.builder().account(phone.toPlainString()).code("123456").build();
            LoginSmsDto loginSmsDto = LoginSmsDto.builder().account(phone.toPlainString()).build();
            openUserController.sms(loginSmsDto);
            openUserController.login(loginDto);
        }
    }

    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IHomeService homeService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Test
    void bindMaster() {

        BigDecimal phone = new BigDecimal("13110000000");

        //20221107095533641938786632118272
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode("20221107095533641938786632118272")
                .build()));
        for (int i = 0; i < 1000; i++) {

            //查询主控设备
            Device device = deviceList.get(i);

            if (userDeviceService.getById(device.getId()) != null) {
                continue;
            }

            phone = phone.add(BigDecimal.ONE);

            //查询用户
            UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(phone.toPlainString())
                    .build()));
            //查询homId；
            Home home = homeService.list(new QueryWrapper<>(Home.builder()
                    .userId(userAccount.getId())
                    .build())).get(0);


            MasterDeviceDto masterDeviceDto = MasterDeviceDto.builder()
                    .homeId(home.getId())
                    .deviceId(device.getId())
                    .build();
            bizUserDeviceService.addMasterDevice(masterDeviceDto, userAccount.getId());
        }
    }


    @Autowired
    private IHomeRoomService homeRoomService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedis() {
        System.out.println(redisTemplate);
        System.out.println(stringRedisTemplate);
    }

    @Test
    void bindIrAndRf() {

        BigDecimal phone = new BigDecimal("13110000000");

        //20221107095533641938786632118272
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode("20221107095533641938786632118272")
                .build()));
        for (int i = 0; i < 1000; i++) {

            phone = phone.add(BigDecimal.ONE);

            //查询用户
            UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(phone.toPlainString())
                    .build()));
            //查询homId；
            HomeRoom homeRoom = homeRoomService.list(new QueryWrapper<>(HomeRoom.builder()
                    .userId(userAccount.getId())
                    .build())).get(0);
            //查询主控设备
            Device device = deviceList.get(i);

            //空调
            UserDeviceAddDto a = UserDeviceAddDto.builder()
                    .masterDeviceId(device.getId())
                    .productId("1569936420509220865")
                    .modelId(1L)
                    .build();

            //窗帘
            UserDeviceAddDto c = UserDeviceAddDto.builder()
                    .masterDeviceId(device.getId())
                    .productId("1565965868811554818")
                    .modelId(1L)
                    .build();


            bizUserDeviceService.add(a, userAccount.getId());
            bizUserDeviceService.add(c, userAccount.getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Test
    void bindMesh() {

        Date start = new Date();

        BigDecimal phone = new BigDecimal("13110000000");

        //20221107095533641938786632118272
        List<Device> deviceList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode("20221107095533641938786632118272")
                .build()));


        //20221107102355641945926692286464
        List<Device> meshList = deviceService.list(new QueryWrapper<>(Device.builder()
                .batchCode("20221107102355641945926692286464")
                .build()));

        for (int i = 0; i < 1000; i++) {

            phone = phone.add(BigDecimal.ONE);

            //查询用户
            UserAccount userAccount = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(phone.toPlainString())
                    .build()));
            //查询homId；
            HomeRoom homeRoom = homeRoomService.list(new QueryWrapper<>(HomeRoom.builder()
                    .userId(userAccount.getId())
                    .build())).get(0);
            //查询主控设备
            Device master = deviceList.get(i);
            Device mesh = meshList.get(i);

            //空调
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("deviceId", mesh.getId());
            body.put("data", data);
            body.put("code", 0);
            HandleMessage message = HandleMessage.builder()
                    .topicDeviceId(master.getId())
                    .body(body)
                    .build();

            bizUserDeviceService.topologyAddDevice(message);
        }


        Date end = new Date();
        System.out.println(end.getTime() - start.getTime());
    }


    @Test
    void mqttTest() {
        for (int i = 0; i < 100; i++) {
            String topic = "sys/1000000100/8f3248c02c0b003/thing/service/topology/delete1";
            MQTT.publish(topic,
                    MqttResultVo.SUCCESS(IdUtils.sId(), " [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating CacheAwareContextLoaderDelegate from class [org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate]\n" +
                            "10:05:04.847 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating BootstrapContext using constructor [public org.springframework.test.context.support.DefaultBootstrapContext(java.lang.Class,org.springframework.test.context.CacheAwareContextLoaderDelegate)]\n" +
                            "10:05:04.932 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating TestContextBootstrapper for test class [com.lj.iot.api.app.AppApiApplicationTests] from class [org.springframework.boot.test.context.SpringBootTestContextBootstrapper]\n" +
                            "10:05:04.968 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Neither @ContextConfiguration nor @ContextHierarchy found for test class [com.lj.iot.api.app.AppApiApplicationTests], using SpringBootContextLoader\n" +
                            "10:05:04.979 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.lj.iot.api.app.AppApiApplicationTests]: class path resource [com/lj/iot/api/app/AppApiApplicationTests-context.xml] does not exist\n" +
                            "10:05:04.981 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.lj.iot.api.app.AppApiApplicationTests]: class path resource [com/lj/iot/api/app/AppApiApplicationTestsContext.groovy] does not exist\n" +
                            "10:05:04.981 [main] INFO org.springframework.test.context.support.AbstractContextLoader - Could not detect default resource locations for test class [com.lj.iot.api.app.AppApiApplicationTests]: no resource found for suffixes {-context.xml, Context.groovy}.\n" +
                            "10:05:04.983 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils - Could not detect default configuration classes for test class [com.lj.iot.api.app.AppApiApplicationTests]: AppApiApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.\n" +
                            "10:05:05.131 [main] DEBUG org.springframework.test.context.support.ActiveProfilesUtils - Could not find an 'annotation declaring class' for annotation type [org.springframework.test.context.ActiveProfiles] and class [com.lj.iot.api.app.AppApiApplicationTests]\n" +
                            "10:05:05.294 [main] DEBUG org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider - Identified candidate component class: file [D:\\code\\new_smarthome\\api\\app-api\\target\\classes\\com\\lj\\iot\\api\\app\\AppApiApplication.class]\n" +
                            "10:05:05.296 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Found @SpringBootConfiguration com.lj.iot.api.app.AppApiApplication for test class com.lj.iot.api.app.AppApiApplicationTests\n" +
                            "10:05:05.544 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - @TestExecutionListeners is not present for class [com.lj.iot.api.app.AppApiApplicationTests]: using defaults.\n" +
                            "10:05:05.545 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Loaded default TestExecutionListener class names from location [META-INF/spring.factories]: [org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener, org.springframework.test.context.web.ServletTestExecutionListener, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener, org.springframework.test.context.event.ApplicationEventsTestExecutionListener, org.springframework.test.context.support.DependencyInjectionTestExecutionListener, org.springframework.test.context.support.DirtiesContextTestExecutionListener, org.springframework.test.context.transaction.TransactionalTestExecutionListener, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener, org.springframework.test.context.event.EventPublishingTestExecutionListener]\n" +
                            "10:05:05.581 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Using TestExecutionListeners: [org.springframework.test.context.web.ServletTestExecutionListener@34a97744, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener@4275c20c, org.springframework.test.context.event.ApplicationEventsTestExecutionListener@7c56e013, org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener@3fc9dfc5, org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener@40258c2f, org.springframework.test.context.support.DirtiesContextTestExecutionListener@2cac4385, org.springframework.test.context.transaction.TransactionalTestExecutionListener@6731787b, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener@16f7b4af, org.springframework.test.context.event.EventPublishingTestExecutionListener@7adf16aa, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener@34a1d21f, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener@58bf8650, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener@73c60324, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener@71ae31b0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener@4ba534b0, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener@6f0ca692]\n" +
                            "10:05:05.587 [main] DEBUG org.springframework.test.context.support.AbstractDirtiesContextTestExecutionListener - Before test class: context [DefaultTestContext@74e47444 testClass = AppApiApplicationTests, testInstance = [null], testMethod = [null], testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@9bd0fa6 testClass = AppApiApplicationTests, locations = '{}', classes = '{class com.lj.iot.api.app.AppApiApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@20f5281c, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4397ad89, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2d778add, org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@44d52de2, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@36060e, org.springframework.boot.test.context.SpringBootTestArgs@1, org.springframework.boot.test.context.SpringBootTestWebEnvironment@71b1176b], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true]], class annotated with @DirtiesContext [false] with mode [null].\n"
                    ).toString()
            );
        }

        try {
            Thread.sleep(100000);
        } catch (Exception e) {
        }

    }
}

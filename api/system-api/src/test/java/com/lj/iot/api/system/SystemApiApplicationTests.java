package com.lj.iot.api.system;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.lj.iot.biz.db.smart.entity.IrDeviceType;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.entity.JDat;
import com.lj.iot.biz.db.smart.service.IIrBrandTypeService;
import com.lj.iot.biz.db.smart.service.IIrDeviceTypeService;
import com.lj.iot.biz.db.smart.service.IIrModelService;
import com.lj.iot.biz.db.smart.service.JDatService;
import com.lj.iot.biz.service.BizWsPublishService;
import com.lj.iot.biz.service.impl.BizIrDataServiceImpl;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class SystemApiApplicationTests {
    @Autowired
    private BizWsPublishService bizWsPublishService;

    @Autowired
    private IIrDeviceTypeService iIrDeviceTypeService;

    @Autowired
    private JDatService jDatService;

    @Test
    void testIrData() {
        try {

            for (int i = 0; i < 100000; i++) {
                String result = OkHttpUtils.get("http://2k8p65.natappfree.cc/ljwl/keyevent.php?mac=ff92e0f2cd6d30a5&kfid=012030&par=0-0-0-0-0-X");
                System.out.println("size=" + i + ",result=" +result);
            }
            //010254    YADOF
            //010252    YBOF2
            //String kfId = "010045";

            /*List<JDat> list = jDatService.getTestIrData(kfId);

            String text = "";
            for (JDat jDat : list
            ) {
                String par = jDat.getTags().split("_")[1];
                // 解析json 获取irdata
                JSONObject irJson = JSONObject.parseObject(jDat.getDats());
                try {
                    String data = BizIrDataServiceImpl.decode(irJson.getString("irdata"));
                    text += "par=" + par + ",data=" + data+"\n";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/


            /*List<JDat> list = jDatService.getTestIrData(kfId);

            String text = "";
            for (JDat jDat : list
            ) {
                String par = jDat.getTags().split("_")[1];
                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("kfid", kfId);
                map.put("par", par);
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/keyevent.php", map);
                System.out.println(result + "," + list.size());
                JSONObject jsonObject = JSON.parseObject(result);
                System.out.println(result);
                String data = jsonObject.getString("irdata");
                data = BizIrDataServiceImpl.decode(data);

                text += "par=" + par + ",data=" + data + "\n";
            }

            String filePath = "C:\\Users\\A\\Desktop\\美的_010045_接口库.txt";

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            writer.write(text);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void contextLoads() {
        bizWsPublishService.publishAllMemberByHomeId("ssss", 7L, "xxxx");
    }


    //同步辉联设备类型
    @Test
    void syncDeviceType() {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("mac", "ff92e0f2cd6d30a5");
            String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getdevicelist.php", map);
            JSONArray jsonArray = JSON.parseArray(result);
            for (Object o : jsonArray) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                IrDeviceType irDeviceType = iIrDeviceTypeService.getOne(new QueryWrapper<>(IrDeviceType.builder()
                        .id(jsonObject.getLong("id"))
                        .build()));
                if (irDeviceType == null) {
                    iIrDeviceTypeService.save(IrDeviceType.builder()
                            .id(jsonObject.getLong("id"))
                            .deviceName(jsonObject.getString("device_name"))
                            .build());
                }
            }
        } catch (Exception e) {
        }
    }

    @Autowired
    private IIrBrandTypeService irBrandTypeService;

    @Test
    void syncTypeBrand() {
        try {
            List<IrDeviceType> deviceTypeList = iIrDeviceTypeService.list();
            for (IrDeviceType irDeviceType : deviceTypeList) {

                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("device_id", irDeviceType.getId() + "");
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getbrandlist.php", map);
                JSONArray jsonArray = JSON.parseArray(result);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                    irBrandTypeService.save(IrBrandType.builder()
                            .brandId(jsonObject.getLong("id"))
                            .deviceTypeId(irDeviceType.getId())
                            .brandName(jsonObject.getString("bn"))
                            .firstLetter(PinyinUtil.getFirstLetter(jsonObject.getString("bn").substring(0, 1), "").toUpperCase())
                            .build());
                }
            }

        } catch (Exception e) {
        }
    }


    @Autowired
    private IIrModelService irModelService;

    @Test
    void syncModel() {
        try {


            List<IrBrandType> brandTypeList = irBrandTypeService.list();
            for (IrBrandType brandType : brandTypeList) {

                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("device_id", brandType.getDeviceTypeId() + "");
                map.put("brand_id", brandType.getBrandId() + "");
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getmodellist.php", map);
                JSONArray jsonArray = JSON.parseArray(result);
                for (Object o : jsonArray) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                    irModelService.save(IrModel.builder()
                            .deviceTypeId(brandType.getDeviceTypeId())
                            .brandId(brandType.getId())
                            .brandName(brandType.getBrandName())
                            .modeName(jsonObject.getString("bn"))
                            .fileId(jsonObject.getString("id"))
                            .build());
                }


            }

        } catch (Exception e) {
        }
    }


    @Test
    void syncFileType() {
        try {


            List<IrModel> modelList = irModelService.list();
            for (IrModel model : modelList) {
                if (model.getFileType() != null) {
                    continue;
                }

                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("kfid", model.getFileId());
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getkeylist.php", map);
                JSONObject jsonObject = JSON.parseObject(result);

                irModelService.update(IrModel.builder()
                        .fileType(jsonObject.getString("Rtype"))
                        .build(), new QueryWrapper<>(IrModel.builder()
                        .fileId(model.getFileId())
                        .build()));

            }

        } catch (Exception e) {
        }
    }

    @Test
    void syncKeyList() {
        try {

            List<IrModel> modelList = irModelService.list(new QueryWrapper<>(IrModel.builder()
                    .deviceTypeId(2L)
                    .build()));
            for (IrModel model : modelList) {

                Map<String, String> map = new HashMap<>();
                map.put("mac", "ff92e0f2cd6d30a5");
                map.put("kfid", model.getFileId());
                String result = OkHttpUtils.get("http://ir.hongwaimaku.com/getkeylist.php", map);
                JSONObject jsonObject = JSON.parseObject(result);

                System.out.println(jsonObject.getString("keylist"));
            }

        } catch (Exception e) {
        }
    }


    @Test
    void syncData() {
        try {

            Map<String, String> map = new HashMap<>();
            map.put("mac", "ff92e0f2cd6d30a5");
            map.put("kfid", "050259");
            map.put("keyid", "1");
            String result = OkHttpUtils.get("http://ir.hongwaimaku.com/keyevent.php", map);
            JSONObject jsonObject = JSON.parseObject(result);
            System.out.println(result);
            String data = jsonObject.getString("irdata");
            System.out.println(BizIrDataServiceImpl.decode(data));
        } catch (Exception e) {
        }
    }


    public static void main(String[] args) {
        /*Long long1 =  Long.valueOf("10000");
        Long long3 =  Long.valueOf("10000");
        long long2=10000L;
        System.out.println(long1.equals(long2));*/


        Integer integer1 = Integer.valueOf("1000");
        Integer integer2 = Integer.valueOf("1000");
        int integer3 = 1000;
        System.out.println(integer1.equals(integer2));
        System.out.println(integer1.equals(integer3));

    }
}

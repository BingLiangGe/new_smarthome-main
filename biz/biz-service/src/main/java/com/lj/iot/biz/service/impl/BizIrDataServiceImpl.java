package com.lj.iot.biz.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.biz.db.smart.entity.JDat;
import com.lj.iot.biz.db.smart.mapper.IrDataMapper;
import com.lj.iot.biz.db.smart.service.JDatService;
import com.lj.iot.biz.db.smart.util.TestLibrary;
import com.lj.iot.biz.service.BizIrDataService;
import com.lj.iot.biz.service.enums.FileTypeEnum;
import com.lj.iot.biz.service.enums.ProductTypeEnum;
import com.lj.iot.common.base.dto.ThingModel;
import com.lj.iot.common.base.dto.ThingModelProperty;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.redis.service.ICacheService;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BizIrDataServiceImpl implements BizIrDataService {

    @Autowired
    private ICacheService cacheService;
    @Autowired
    private JDatService jDatService;

    @Autowired
    private IrDataMapper irDataMapper;

    @Override
    public List<IrData> getDjAirContData(String fileIds) {
        return irDataMapper.getDjAirContData(fileIds);
    }

    @Override
    public List<IrModel> getDjAirContr() {
        return irDataMapper.getIrDataDjiAirContr();
    }

    @Override
    public String getIrData(String productType, IrModel irModel, ThingModel thingModel, Integer keyIdx) {

        String dataIndex = getDataIndex(productType, irModel, thingModel, keyIdx);

        String data = cacheService.get("IR_CODE:" + irModel.getFileId() + ":" + dataIndex);
        if (data != null) {
            return data;
        }

        // 自学码
        if (checkSelfStudyCode(irModel.getFileId())) {
            JDat jDat = null;
            if ("010003".equals(irModel.getFileId())) {
                jDat = jDatService.getOne(new QueryWrapper<>(JDat.builder().tags(irModel.getFileId() + "_" + "X-X-X-X-X-" + dataIndex).build()), false);
            } else {
                jDat = jDatService.getOne(new QueryWrapper<>(JDat.builder().tags(irModel.getFileId() + "_" + dataIndex).build()), false);
            }
            ValidUtils.isNullThrow(jDat, "当前遥控器无此按键");
            return jDat.getDats();
        }
        //通过接口获取码[解码,保存,返回]
        data = syncData(irModel.getFileId(), dataIndex);
        ValidUtils.isNullThrow(data, "当前遥控器无此按键");

        cacheService.addSeconds("IR_CODE:" + irModel.getFileId() + ":" + dataIndex, data, 300);

        return data;

       /*
        todo 本地码库
        JDat jDat =null;
        if ("010003".equals(irModel.getFileId())){
            jDat = jDatService.getOne(new QueryWrapper<>(JDat.builder().tags(irModel.getFileId() + "_" + "X-X-X-X-X-" + dataIndex).build()), false);
        }else{
            jDat = jDatService.getOne(new QueryWrapper<>(JDat.builder().tags(irModel.getFileId() + "_" + dataIndex).build()), false);
        }

        *//*
        //通过接口获取码[解码,保存,返回]
        data = syncData(irModel.getFileId(), dataIndex);

        ValidUtils.isNullThrow(data, "当前遥控器无此按键");

        cacheService.addSeconds("IR_CODE:" + irModel.getFileId() + ":" + dataIndex, data, 300);

        return data;*//*

        // 存在对应码
        if (jDat != null) {

            // 自学码
            if (checkSelfStudyCode(irModel.getFileId())) {
                data = jDat.getDats();
            } else {
                // 解析json 获取irdata
                JSONObject irJson = null;
                try {
                    irJson = JSONObject.parseObject(jDat.getDats());
                    data = decode(irJson.getString("irdata"));
                } catch (JSONException e) {
                    try {
                        data = decode(jDat.getDats());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        log.info("解析出错1 jDat={}", jDat);
                        ValidUtils.isNullThrow(e, "当前遥控器无此按键");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("解析出错 jDat={}", jDat);
                    ValidUtils.isNullThrow(e, "当前遥控器无此按键");
                }
            }
            cacheService.addSeconds("IR_CODE:" + irModel.getFileId() + ":" + dataIndex, data, 300);
            return data;
        }

        log.info("jDatisNull,fileId={},dataIndex={}", irModel.getFileId(), dataIndex);
        // 不存在对应码则报错
        ValidUtils.isNullThrow(data, "当前遥控器无此按键");

        return null;*/
    }

    /**
     * 验证是否为自学码
     *
     * @param fileId
     * @return
     */
    public boolean checkSelfStudyCode(String fileId) {

        String[] saleStudys = {"999991", "999992", "999998", "999981", "999980", "999970", "999999", "9999991"
                , "9999992", "9999993", "9999994", "9999995", "9999996", "9999997", "9999998", "9999999", "99999910"
                , "99999911", "99999912", "99999913", "99999914", "99999915", "99999916", "99999917", "99999918", "99999919"
                , "99999920", "99999921", "99999922", "99999923", "99999924", "70000000" ,"065555"};

        for (String code : saleStudys
        ) {
            // 为自学码
            if (code.equals(fileId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDataIndex(String productType, IrModel irModel, ThingModel thingModel, Integer keyIndex) {
        String dataIndex = keyIndex + "";

        //空调的需要特殊处理(空调全属性控制)
        if (ProductTypeEnum.AC.getCode().equals(productType)) {
            Map<String, ThingModelProperty> thingModelMap = thingModel.thingModel2Map();
            try {
                int i = 0;
                int powerstate = NumberUtil.parseInt(thingModelMap.get("powerstate").getValue() + "");
                if (powerstate == 0) {
                    i = 1;
                }
                //dataIndex = NumberUtil.parseInt(thingModelMap.get("powerstate").getValue()+"") +
                dataIndex = NumberUtil.parseInt(i + "") +
                        "-" + NumberUtil.parseInt(thingModelMap.get("workmode").getValue() + "") +
                        "-" + (NumberUtil.parseInt(thingModelMap.get("temperature").getValue() + "") - 16) +
                        "-" + NumberUtil.parseInt(thingModelMap.get("fanspeed").getValue() + "") +
                        "-" + NumberUtil.parseInt(thingModelMap.get("airdirection").getValue() + "") +
                        "-";
            } catch (Exception e) {
                log.error("BizIrDataServiceImpl.getDataIndex", e);
                throw CommonException.FAILURE("空调设备物模型数据有误");
            }
            if (FileTypeEnum.SIX_X.getCode().equals(irModel.getFileType())) {
                dataIndex += "X";
            } else if (FileTypeEnum.SIX_N.getCode().equals(irModel.getFileType())) {
                dataIndex += keyIndex;
            } else if (FileTypeEnum.KEY_3.getCode().equals(irModel.getFileType())) {
                dataIndex = keyIndex + "";
            } else {
                throw CommonException.FAILURE("红外码类型不正确,联系管理员处理");
            }
        }
        return dataIndex;
    }

    @Override
    public String syncData(String fileId, String dataIndex) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("mac", "ff92e0f2cd6d30a5");

            if (dataIndex.indexOf("-") == -1) {
                map.put("keyid", dataIndex);
            } else {
                map.put("par", dataIndex);
            }
            map.put("kfid", fileId);
            String result = OkHttpUtils.get("http://47.100.238.205/ljwl/keyevent.php", map);
            JSONObject jsonObject = JSON.parseObject(result);
            String data = jsonObject.getString("irdata");

            ValidUtils.isNullThrow(data, "不支持该品牌型号");
            //解码
            return decode(data);

        } catch (Exception e) {
            log.error("BizIrDataServiceImpl.syncData", e);
            throw CommonException.FAILURE("不支持该品牌型号");
        }
    }

    static char pwda[] = {
            0x88, 0x9E, 0xA3, 0x31, 0x72, 0xEE, 0xDC, 0x50, 0x0E, 0xA7, 0x94, 0xEB, 0x34, 0x7A, 0xB9, 0x12,
            0x8F, 0xAB, 0x87, 0xCE, 0x20, 0xC2, 0x7E, 0x85, 0x11, 0x6B, 0xC7, 0x62, 0x2F, 0x73, 0x06, 0xDD,
            0x69, 0x6A, 0x4D, 0x84, 0xE3, 0x29, 0x17, 0x25, 0x10, 0x0F, 0x08, 0x3E, 0x4F, 0xE9, 0x36, 0x38,
            0x0D, 0x82, 0xA6, 0xB4, 0x7F, 0x04, 0x63, 0xC9, 0x22, 0x66, 0x6F, 0x86, 0x3F, 0x59, 0xDE, 0x47,
            0x77, 0xDB, 0xC6, 0x58, 0x7B, 0x8C, 0x9A, 0xC5, 0x83, 0x23, 0xD4, 0xB1, 0xA8, 0xAF, 0xFA, 0xAE,
            0x43, 0x1E, 0xF0, 0xEF, 0xC0, 0x26, 0x07, 0x79, 0x75, 0x2A, 0xBD, 0x27, 0xD7, 0xDA, 0xAC, 0xAA,
            0xE1, 0xE6, 0xDF, 0x89, 0xC4, 0x92, 0x6E, 0xE8, 0xF9, 0x56, 0xB3, 0xA2, 0x44, 0xD5, 0xCC, 0x1D,
            0x71, 0xCD, 0x45, 0x70, 0x51, 0x68, 0xFB, 0x4B, 0x97, 0x65, 0x2C, 0x6C, 0x0A, 0xC3, 0xFC, 0xD3,
            0x09, 0x46, 0x95, 0xED, 0xE0, 0x8A, 0xB6, 0x2E, 0xE7, 0x7D, 0x42, 0xCA, 0x53, 0x5C, 0x4C, 0x93,
            0xD8, 0x49, 0x96, 0xF2, 0xD6, 0x6D, 0x1C, 0x14, 0x91, 0x35, 0x3D, 0xD9, 0x21, 0xE4, 0x8B, 0x18,
            0x40, 0x98, 0x37, 0xFD, 0x9B, 0xF7, 0xA1, 0x2D, 0xBC, 0x55, 0x4A, 0x33, 0x9D, 0x24, 0xF6, 0xB7,
            0x39, 0x99, 0x8E, 0x5E, 0xBF, 0x41, 0xFF, 0x19, 0x02, 0xCF, 0xBA, 0x3C, 0x28, 0xE5, 0x15, 0x5D,
            0x30, 0x5B, 0xD2, 0xEA, 0xA9, 0x81, 0xB8, 0x61, 0x05, 0x2B, 0x13, 0xD0, 0xAD, 0x3B, 0xF4, 0x01,
            0x0B, 0xA0, 0x90, 0xFE, 0x1B, 0x64, 0x1A, 0x16, 0xC1, 0x67, 0x5F, 0xF1, 0xF8, 0x48, 0x3A, 0x60,
            0x54, 0xB0, 0xCB, 0x8D, 0x9C, 0xA4, 0xB2, 0x0C, 0xF5, 0xF3, 0x00, 0x32, 0xC8, 0x57, 0xEC, 0x74,
            0x4E, 0x7C, 0x76, 0xBE, 0x80, 0xA5, 0xB5, 0xE2, 0x9F, 0x1F, 0xD1, 0x5A, 0x52, 0xBB, 0x03, 0x78
    };

    static char pwdb[] = {
            0x89, 0x9A, 0xB0, 0x09, 0xE9, 0x84, 0x26, 0x7E, 0x7C, 0x55, 0x92, 0xF3, 0x1E, 0xBA, 0xEC, 0x6C,
            0x72, 0x11, 0x76, 0x14, 0x73, 0xB7, 0xA0, 0x1F, 0xF5, 0x12, 0xA8, 0xBE, 0xE2, 0x21, 0xEF, 0xA5,
            0x33, 0x2E, 0xFF, 0x29, 0x38, 0x70, 0x8A, 0xF4, 0xB9, 0xE1, 0x82, 0xE4, 0xDB, 0xEB, 0x1D, 0x52,
            0xFD, 0x98, 0x64, 0xAD, 0x93, 0x36, 0x4E, 0x3E, 0x5B, 0x3D, 0x04, 0xA4, 0xA9, 0x17, 0x1B, 0xA7,
            0xD8, 0x78, 0x71, 0x99, 0x19, 0x01, 0x6F, 0x8B, 0x79, 0xA3, 0x77, 0x24, 0xF2, 0x85, 0xC6, 0x31,
            0x9B, 0xC3, 0x8D, 0x37, 0xA6, 0xD2, 0x35, 0x83, 0xFE, 0x6D, 0xFA, 0x4A, 0x62, 0xF1, 0x69, 0x03,
            0x6A, 0x0F, 0x42, 0x97, 0x43, 0x4B, 0x53, 0x91, 0xCB, 0x5E, 0xD7, 0x68, 0x34, 0x8E, 0x7D, 0xCD,
            0x57, 0x95, 0x39, 0x2A, 0x49, 0x06, 0x1A, 0x63, 0xE5, 0x0D, 0x66, 0x5F, 0x32, 0xB4, 0x54, 0xD3,
            0x44, 0x65, 0x9C, 0xD9, 0x9F, 0xD1, 0x60, 0x05, 0x3B, 0x13, 0xC5, 0x46, 0x41, 0xF6, 0xC0, 0xBD,
            0xCA, 0x08, 0xFC, 0x9D, 0x0A, 0xD0, 0xBF, 0x6B, 0x67, 0x27, 0xAC, 0x61, 0x9E, 0x59, 0x7B, 0x0B,
            0x5A, 0xC7, 0x15, 0x86, 0xEE, 0x48, 0x1C, 0xE3, 0x47, 0xEA, 0x8F, 0x2F, 0xC8, 0x2B, 0x87, 0xC1,
            0x22, 0xF7, 0x5D, 0x07, 0x56, 0xC4, 0xF8, 0xE0, 0x10, 0xAF, 0x8C, 0xCF, 0x00, 0x02, 0xF9, 0x16,
            0xD6, 0x23, 0x4F, 0x58, 0x75, 0x7F, 0x0C, 0xD4, 0x2D, 0xE7, 0xE8, 0x0E, 0x7A, 0xAE, 0xB8, 0x88,
            0x30, 0x90, 0x45, 0xB2, 0x40, 0xE6, 0x74, 0xAA, 0xB3, 0x5C, 0xED, 0xBB, 0xDE, 0x4C, 0xB5, 0x3A,
            0x51, 0x3C, 0xC2, 0x50, 0xB6, 0xA2, 0x3F, 0xF0, 0x2C, 0xDC, 0xDA, 0x94, 0xDF, 0xAB, 0x28, 0x81,
            0xBC, 0xFB, 0x25, 0xB1, 0xCC, 0xA1, 0xD5, 0x18, 0x80, 0x96, 0xC9, 0xCE, 0x20, 0x6E, 0x4D, 0xDD
    };

    static char pwdc[] = {
            0x80, 0x54, 0xA3, 0xCE, 0xB2, 0xCB, 0x6B, 0x4A, 0x3E, 0xB1, 0x96, 0x8C, 0x7A, 0x08, 0x99, 0x18,
            0x0E, 0x33, 0x30, 0x8D, 0x15, 0x13, 0x51, 0xB4, 0x3B, 0x7E, 0xAD, 0x59, 0x65, 0x60, 0x4E, 0x6E,
            0xF2, 0x3A, 0xF7, 0xCC, 0x27, 0x52, 0x26, 0xF3, 0xD8, 0x35, 0x24, 0xEB, 0xDF, 0x90, 0x86, 0x9D,
            0x38, 0xF9, 0x31, 0x0D, 0x5F, 0x87, 0x3F, 0xE8, 0xD1, 0xE9, 0x8E, 0xA6, 0xBA, 0xEA, 0xC8, 0x36,
            0xA7, 0xC1, 0x6D, 0x5E, 0x82, 0xEC, 0x4C, 0xB0, 0xDB, 0xA8, 0x00, 0x17, 0x4B, 0x32, 0x37, 0xA2,
            0x91, 0xA0, 0x34, 0x58, 0x92, 0x03, 0xE6, 0x8B, 0xFA, 0xE0, 0x5D, 0xC4, 0xDA, 0x95, 0xEE, 0x45,
            0xD0, 0xDD, 0x70, 0xFC, 0xB8, 0x0C, 0xCA, 0xCF, 0xBF, 0x3D, 0x5A, 0xC3, 0xC0, 0xF8, 0x76, 0xAB,
            0xFB, 0x40, 0x44, 0xB6, 0x71, 0xD7, 0xE7, 0x3C, 0x8F, 0x79, 0x84, 0xC7, 0x66, 0x22, 0x20, 0x41,
            0x25, 0x9C, 0x2F, 0x74, 0xC9, 0x9F, 0x29, 0x1E, 0x7C, 0xED, 0x55, 0x63, 0xDE, 0x2C, 0x16, 0x19,
            0x6F, 0x05, 0xE3, 0x6C, 0xA1, 0x0B, 0x4F, 0x53, 0x2E, 0x09, 0x46, 0xC2, 0x85, 0x62, 0xF4, 0x88,
            0x9B, 0x69, 0xB9, 0x1A, 0x68, 0x81, 0xBE, 0xD2, 0xB3, 0x93, 0xF0, 0x39, 0x4D, 0x5C, 0x1C, 0x67,
            0x1B, 0x56, 0x47, 0x7D, 0x61, 0x97, 0x49, 0x7B, 0xC5, 0xCD, 0xAE, 0x06, 0xFE, 0x2D, 0xA4, 0x50,
            0x94, 0x8A, 0x73, 0x04, 0xA9, 0x64, 0x98, 0xD4, 0xAA, 0xB5, 0x43, 0x57, 0x1D, 0xF1, 0x78, 0xC6,
            0xE5, 0x89, 0xE2, 0x0F, 0x07, 0x7F, 0x83, 0xAC, 0x42, 0xFD, 0x0A, 0x28, 0xAF, 0xBD, 0x12, 0xDC,
            0x23, 0x2B, 0xF6, 0xEF, 0xD5, 0xB7, 0x9A, 0xD6, 0x75, 0xF5, 0x72, 0x21, 0xA5, 0x1F, 0x6A, 0x5B,
            0x2A, 0xE4, 0x01, 0x14, 0x9E, 0x48, 0x77, 0xBB, 0x10, 0xFF, 0xE1, 0xD9, 0xD3, 0x02, 0xBC, 0x11
    };

    public static String decode(String srcDataHis) {
        log.info("decode start srcDataHis={}", srcDataHis);
        if (StringUtils.isBlank(srcDataHis)) {
            return null;
        }
        String[] split = srcDataHis.split(",");
        byte[] srcData = new byte[split.length];
        int dataLen = srcData.length;
        for (int i = 0; i < split.length; i++) {
            String s = split[i].replaceAll(" ", "").replaceAll("\n", "").trim();
            if (StringUtils.isBlank(s)) {
                dataLen--;
                continue;
            }
            srcData[i] = (byte) Integer.parseInt(s, 16);
            //System.out.print(srcData[i]+" ");
        }
        //System.out.println();
        byte[] dstData = new byte[256];
        int K1, K2, K3, pv;
        int scnt = 0;
        K1 = srcData[0];
        K2 = (dataLen - 1);
        dstData[0] = (byte) K2;

        for (K3 = 1; K3 < dataLen; K3++) {
            if (K1 < 0) {
                K1 += 256;
            }
            pv = (pwda[K3] + pwdb[K2] - pwdc[K1]) % 256;
            dstData[K3] = (byte) (srcData[K3] ^ pv);
        }

        if (dstData[3] != 0) {
            scnt++;
            if (scnt % 2 != 0) {
                if (!"ff".equalsIgnoreCase(byteToHex(dstData[3]))) {
                    int tempIndex = dstData[3];
                    if (tempIndex < 0) {
                        tempIndex += 256;
                    }
                    dstData[tempIndex] = (byte) (dstData[tempIndex] ^ dstData[4]);
                } else {
                    K1 = dstData[26];
                    K2 = dstData[27];
                    K3 = dstData[28];
                    dstData[26] = dstData[29];
                    dstData[27] = dstData[30];
                    dstData[28] = dstData[31];
                    dstData[29] = dstData[32];
                    dstData[30] = (byte) K1;
                    dstData[31] = (byte) K2;
                    dstData[32] = (byte) K3;
                }
            }
        }

        int realLenth = dstData.length;
        for (int i = dstData.length - 1; i >= 0; i--) {
            if ((int) dstData[i] != 0) {
                realLenth = i + 2;//加一个后缀0
                break;
            }
        }
        String[] result = new String[realLenth];
        for (int i = 0; i < realLenth; i++) {
            result[i] = byteToHex1(dstData[i]);
        }
        String ret = String.join(",", result);
        log.info("decode end ret={}", ret);
        return ret;
    }

    public static String decode_bak(String srcDataHis) {
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

    public static String byteToHex1(byte n) {
        try {
            return byteToHex(n & 0xFF);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(n);
        }
        return "";
    }

    public static String byteToHex(int n) {
        if (n < 0) {
            n = n + 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexArray[d1] + hexArray[d2];
    }

    public String transportIrData(String str) {
        byte[] strArr = new byte[0];
        String resMsg = null;
        try {
            strArr = hexStr2Bytes(str);
            byte[] dstData = new byte[strArr.length];
            TestLibrary.INSTANCE.convRemotcode(strArr, dstData, strArr.length);
            String[] result = new String[dstData.length];
            int i = 0;
            for (byte b :
                    dstData) {
                String format = String.format("%02x", b & 0xff);
                result[i] = format.length() == 2 ? format : "0" + format;
                i++;
                System.out.println(format);
            }
            resMsg = String.join(",", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resMsg;

    }

    /**
     * bytes字符串转换为Byte值
     *
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) throws Exception {
        src = src.trim().replaceAll(",", "");
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int b = Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
            ret[i] = (byte) b;
        }
        return ret;
    }


    public static void main(String[] args) {
        String decode = decode("7D,53,5C,EA,32,A7,AD,8B,F7,62,7D,26,DD,35,E2,0A,59,F0,82,87,C8,EF,F4,3E,EA,2A,8E,0E,02,EE,BF,21,6A,E2,17,A9,5E,E2,C1,4A,0A,C8,C0,F6,08");

        System.out.println(decode);
    }

}

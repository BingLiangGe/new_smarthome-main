package com.lj.iot.biz.service.mahjong;


import cn.hutool.core.util.HexUtil;
import com.lj.iot.common.base.enums.MahjongMachineEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能麻将机测试类
 */
@Slf4j
public class MahjongMachineUtil {

    public static void main(String[] args) {
        System.out.println("sendMachineData=" + sendMachineData("powerstate", 1));

        System.out.println(getPositionData(108));
    }


    public static int getPositionData(Integer type) {
        Map<Integer, String> dataMap = MahjongMachineEnum.POSITION.getValues();

        for (Integer key : dataMap.keySet()
        ) {
            if (dataMap.get(key).equals(String.valueOf(type))) {
                return key;
            }
        }
        return 0;
    }

    /**
     * 发送麻将数据
     *
     * @param type  类型
     * @param value 值
     * @return
     */
    public static String[] sendMachineData(String type, Integer value) {


        MahjongMachineEnum titles[] = MahjongMachineEnum.values();

        // 处理标题
        for (MahjongMachineEnum title : titles
        ) {
            if (title.getCode().equals(type)) {
                String data2_16 = title.getValues().get(value);
                /*if ("position".equals(type)){
                    data2_16=19;
                }*/


                Map<String, String> datas = createData(data2_16, type, value);

                int data1 = hexToInteger(datas.get("data1"));
                int data2 = hexToInteger(datas.get("data2"));
                int data3 = hexToInteger(datas.get("data3"));

                int data4_10 = data1 + data2 + data3;

                datas.put("data4", toHex(data4_10, 2));

                // 档位特殊处理
                if ("position".equals(type)) {
                    datas.put("data2", toHex(value, 2));
                    datas.put("data1", "B0");
                }

                String[] dataArray = {datas.get("data1"), datas.get("data2"), datas.get("data3"), datas.get("data4")};

                return dataArray;
            }
        }
        return null;
    }

    public static Map<String, String> createData(String data2, String code, Integer value) {
        // 档位特殊处理
        return new HashMap() {{
            put("data1", "A0");
            put("data2", data2);
            put("data3", "01");
        }};
    }

    /**
     * 10进制转16进制 长度为自定义，满足不同的需求， 0填充在左侧
     *
     * @param serialNum 需要被转换的数字
     * @param length    需要转换成的长度
     * @return 左侧为0的自定义长度的16进制
     * @author hjm
     * @date 2023-05-16
     */
    public static String toHex(int serialNum, int length) {
        return String.format("%0" + length + "x", serialNum);
    }


    /**
     * 16进制转10进制
     *
     * @param serialNum 需要被转换的16进制
     * @return 10进制
     * @author hjm
     * @date 2023-05-16
     */
    public static Integer hexToInteger(String serialNum) {
        return HexUtil.hexToInt(serialNum);
    }
}

package com.lj.iot.biz.service.mahjong;

import com.lj.iot.common.base.enums.HeatingTableEnum;

/**
 * 取暖桌工具类
 */
public class HeatingTableUtil {

    public static void main(String[] args) {
        System.out.println(sendMachineData("left_front"));
    }

    public static String sendMachineData(String type) {

        HeatingTableEnum titles[] = HeatingTableEnum.values();

        // 处理标题
        for (HeatingTableEnum title : titles
        ) {
            if (title.getCode().equals(type)) {
                return title.getCommend();
            }
        }
        return null;
    }
}

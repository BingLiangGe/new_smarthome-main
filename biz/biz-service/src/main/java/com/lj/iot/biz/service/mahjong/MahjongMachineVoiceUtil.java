package com.lj.iot.biz.service.mahjong;


import cn.hutool.core.util.HexUtil;
import com.lj.iot.common.base.enums.MahjongMachineEnum;
import com.lj.iot.common.base.enums.MahjongMachineVoiceEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 声控麻将机测试类
 */
@Slf4j
public class MahjongMachineVoiceUtil {

    public static void main(String[] args) {
        String[] datas = sendMachineData("position", 1);

        for (String value:datas
             ) {
            System.out.print(value+",");
        }
        System.out.println("sendMachineData=" +datas);

    }


    /**
     * 发送麻将数据
     *
     * @param type  类型
     * @param value 值
     * @return
     */
    public static String[] sendMachineData(String type, Integer value) {


        MahjongMachineVoiceEnum titles[] = MahjongMachineVoiceEnum.values();

        // 处理标题
        for (MahjongMachineVoiceEnum title : titles
        ) {
            if (title.getCode().equals(type)) {

                return title.getValues().get(value).getCode().split(",");
            }
        }
        return null;
    }
}

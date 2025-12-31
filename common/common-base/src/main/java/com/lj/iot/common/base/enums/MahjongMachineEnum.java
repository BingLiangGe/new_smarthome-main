package com.lj.iot.common.base.enums;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 麻将thimodel
 */
public enum MahjongMachineEnum {

    POWERSTATE("powerstate", "开关", new HashMap() {{
        put(1, "01");
        put(0, "02");
    }}),
    PALMING("palming", "藏牌", new HashMap() {{
        put(1, "03");
        put(0, "04");
    }}),
    CHROMOPHORE("chromophore", "打色子", new HashMap() {{
        put(0, "05");
        put(1, "06");
        put(2, "07");
        put(3, "08");
    }}),
    MODEL("model", "模式", new HashMap() {{
        put(0, "0C");
    }}),
    BUZZER("buzzer", "蜂鸣器", new HashMap() {{
        put(0, "0B");
    }}),
    OPERATION("operation", "操作盘状态", new HashMap() {{
        put(1, "09");
        put(0, "0A");
    }}),
    POSITION("position", "档位", new HashMap() {{
        put(1, "18");
        put(2, "160");
        put(3, "152");
        put(4, "148");
        put(5, "144");
        put(6, "142");
        put(7, "140");
        put(8, "138");
        put(9, "136");
        put(10, "132");
        put(11, "128");
        put(12, "126");
        put(13, "124");
        put(14, "120");
        put(15, "116");
        put(16, "112");
        put(17, "110");
        put(19, "108");
        put(20, "104");
        put(21, "102");
        put(22, "102");
        put(23, "100");
        put(25, "96");
        put(26, "92");
        put(27, "88");
        put(30, "84");
        put(31, "80");
        put(33, "72");
        put(34, "54");
        put(35, "40");
        put(37, "36");
        put(38, "32");
        put(39, "20");
    }});


    private String code;
    private String name;

    private Map<Integer, String> values;

    MahjongMachineEnum(String code, String name, Map<Integer, String> values) {
        this.code = code;
        this.name = name;
        this.values = values;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, String> getValues() {
        return values;
    }

    public void setValues(Map<Integer, String> values) {
        this.values = values;
    }
}

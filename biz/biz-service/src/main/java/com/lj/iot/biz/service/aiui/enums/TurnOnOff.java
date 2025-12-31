package com.lj.iot.biz.service.aiui.enums;

public enum TurnOnOff {
    TurnOff("turnOff", "关闭"),
    TurnOn("turnOn", "打开");

    public final String code;
    public final String value;

    TurnOnOff(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static TurnOnOff parse(String code) {
        for (TurnOnOff item : TurnOnOff.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

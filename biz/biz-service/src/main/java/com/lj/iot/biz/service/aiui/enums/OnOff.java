package com.lj.iot.biz.service.aiui.enums;

public enum OnOff {
    TurnOff("0", "关闭"),
    TurnOn("1", "打开"),
    Stop("2", "暂停");

    public final String code;
    public final String value;

    OnOff(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}

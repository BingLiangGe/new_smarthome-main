package com.lj.iot.biz.service.aiui.enums;

public enum Adjust {

    add("0", "add"),
    reduce("1", "reduce");

    public final String code;
    public final String value;

    Adjust(String code, String value) {
        this.code = code;
        this.value = value;
    }
}

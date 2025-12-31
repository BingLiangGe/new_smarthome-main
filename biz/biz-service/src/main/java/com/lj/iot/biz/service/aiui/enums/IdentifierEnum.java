package com.lj.iot.biz.service.aiui.enums;

public enum IdentifierEnum {

    /**
     * 摆风
     */
    oscillating_switch("oscillatingswitch", "摆风"),
    /**
     * 风速
     */
    fan_speed("fanspeed", "风速");

    public final String code;
    public final String value;

    IdentifierEnum(String code, String value) {
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

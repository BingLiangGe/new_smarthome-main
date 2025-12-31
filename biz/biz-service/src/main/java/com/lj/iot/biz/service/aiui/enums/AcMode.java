package com.lj.iot.biz.service.aiui.enums;

public enum AcMode {

    /**
     * 自动
     */
    auto("0", "auto"),
    /**
     * 制冷
     */
    cool("1", "cool"),
    /**
     * 送风
     */
    fan("2", "fan"),
    /**
     * 除湿
     */
    dry("3", "dry"),

    /**
     * 制热
     */
    hot("4", "hot");

    public final String code;
    public final String value;

    AcMode(String code, String value) {
        this.code = code;
        this.value = value;
    }
}

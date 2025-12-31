package com.lj.iot.common.base.enums;

public enum PlatFormEnum {

    APP("app", "APP"),
    SYS("sys", "管理后台"),
    HOTEL("hotel", "酒店"),
    FOURFRIENDS("fourFriends", "四个朋友"),
    HOTELWECHAT("hotelWechat", "小程序"),
    HOTELWECHATSYS("hotelWechatSys", "小程序后台管理")
    ;

    private String code;
    private String desc;

    PlatFormEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

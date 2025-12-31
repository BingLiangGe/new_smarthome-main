package com.lj.iot.common.base.enums;

public enum CommonCodeEnum {

    SUCCESS(0, "成功"),
    FAILURE(-1, "失败"),
    LOGIN_INFO_NOT_EXIST(50, "需要跳转登录页面"),
    NOT_EXIST(100, "数据不存在"),

    SUB_NOT_EXIST(150, "子设备数据不存在"),

    KEEP_SESSION(10000, "保持回话");


    private Integer code;
    private String desc;

    CommonCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

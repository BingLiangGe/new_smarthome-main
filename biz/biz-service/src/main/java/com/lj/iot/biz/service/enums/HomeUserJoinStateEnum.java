package com.lj.iot.biz.service.enums;

public enum HomeUserJoinStateEnum {

    /**
     * 待处理
     */
    PENDING("10", "待处理"),

    /**
     * 同意
     */
    AGREE("20", "同意"),

    /**
     * 拒绝
     */
    REFUSE("30", "拒绝");
    /**
     * code
     */
    private String code;

    /**
     * 描述
     */
    private String desc;

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

    HomeUserJoinStateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HomeUserJoinStateEnum parse(String code) {
        for (HomeUserJoinStateEnum item : HomeUserJoinStateEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

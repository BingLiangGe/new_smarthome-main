package com.lj.iot.biz.service.enums;

public enum HomeUserJoinActionEnum {

    /**
     * 申请
     */
    APPLY("apply", "申请"),

    /**
     * 邀请
     */
    INVITE("invite", "邀请");
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

    HomeUserJoinActionEnum(String topic, String desc) {
        this.code = topic;
        this.desc = desc;
    }

    public static HomeUserJoinActionEnum parse(String topic) {
        for (HomeUserJoinActionEnum item : HomeUserJoinActionEnum.values()) {
            if (item.code.equals(topic)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.service.enums;

/**
 * @author
 * @since 1.0.0
 */
public enum ModeEnum {
    /**
     * 等于
     */
    EQ("=", "等于"),

    /**
     * 加
     */
    ADD("+", "加"),

    /**
     * 减
     */
    REDUCE("-", "减"),

    /**
     * 循环
     */
    LOOP("loop", "循环");

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

    ModeEnum(String topic, String desc) {
        this.code = topic;
        this.desc = desc;
    }

    public static ModeEnum parse(String code) {
        for (ModeEnum item : ModeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

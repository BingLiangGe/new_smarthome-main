package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum OperationEnum {

    /**
     * 语音控制
     */
    AI_C("AI_C", "语音控制"),


    /**
     * APP控制
     */
    APP_C("APP_C", "APP控制"),

    /**
     * APP控制
     */
    THIRD_PARTY("THIRD_PARTY", "第三方控制"),

    /**
     * 定时控制
     */
    Q_C("Q_C", "定时控制"),

    /**
     * APP场景控制
     */
    APP_S_C("APP_S_C", "APP场景控制"),


    /**
     * 定时场景控制
     */
    Q_S_C("Q_S_C", "定时场景控制"),

    /**
     * 情景面板场景控制
     */
    S_S_C("S_S_C", "情景面板场景控制"),

    /**
     * 语音场景控制
     */
    AI_S_C("AI_S_C", "语音场景控制");



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

    OperationEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationEnum parse(String code) {
        for (OperationEnum item : OperationEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

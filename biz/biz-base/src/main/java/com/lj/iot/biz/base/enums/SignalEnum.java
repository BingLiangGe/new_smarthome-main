package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum SignalEnum {
    /**
     * 红外
     */
    IR("IR", "红外"),

    /**
     * 射频
     */
    RF("RF", "射频"),

    /**
     * 虚设备
     */
    INVENTED("INVENTED", "虚设备"),

    /**
     * 蓝牙
     */
    MESH("MESH", "蓝牙"),

    /**
     * 主控
     */
    MASTER("MASTER", "主控");
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

    SignalEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SignalEnum parse(String code) {
        for (SignalEnum item : SignalEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

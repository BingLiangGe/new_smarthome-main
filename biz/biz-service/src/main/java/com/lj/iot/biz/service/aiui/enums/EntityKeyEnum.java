package com.lj.iot.biz.service.aiui.enums;

public enum EntityKeyEnum {

    /**
     * RGB色彩
     */
    new_rgb_color("new_rgb_color", "RGB色彩"),
    /**
     * 百叶窗调节
     */
    shutter_adjust("shutter_adjust", "百叶窗调节"),
    /**
     * 智能床模式
     */
    new_bed_mode("new_bed_mode", "智能床模式"),
    /**
     * 智能床调节
     */
    new_bed_adjust("new_bed_adjust", "智能床调节"),
    /**
     * 空调模式
     */
    new_ac_mode("new_ac_mode", "空调模式"),
    /**
     * 风速
     */
    new_fanspeed("new_fanspeed", "风速"),

    /**
     * 窗帘打开程度
     */
    new_degree("new_degree", "窗帘打开程度");

    public final String code;
    public final String value;

    EntityKeyEnum(String code, String value) {
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

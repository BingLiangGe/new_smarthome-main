package com.lj.iot.biz.service.enums;

public enum ProductTypeEnum {

    /**
     * 网关
     */
    GATEWAY("gateway", "网关"),

    /**
     * 风扇
     */
    FAN("fan", "风扇"),

    /**
     * 灯
     */
    LIGHT("light", "灯"),

    /**
     * 电视盒子
     */
    BOX("box", "电视盒子"),

    /**
     * 空调
     */
    AC("airControl", "空调"),

    /**
     * 新风机
     */
    VENTILATION("ventilation", "新风机"),

    /**
     * 百叶窗
     */
    SHUTTER("shutter", "百叶窗"),

    /**
     * 智能床
     */
    BED("bed", "智能床"),

    /**
     * 窗帘MESH
     */
    CURTAIN("curtain", "电动窗帘");
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

    ProductTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProductTypeEnum parse(String code) {
        for (ProductTypeEnum item : ProductTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

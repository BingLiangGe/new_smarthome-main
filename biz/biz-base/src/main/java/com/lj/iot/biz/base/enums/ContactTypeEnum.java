package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum ContactTypeEnum {
    /**
     * 物业
     */
    property("property", "物业"),

    /**
     * 亲属
     */
    relatives("relatives", "亲属");
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

    ContactTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ContactTypeEnum parse(String code) {
        for (ContactTypeEnum item : ContactTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

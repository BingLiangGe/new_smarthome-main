package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum NoticeTypeEnum {

    /**
     * 商品
     */
    CALL(1, "商品/前台"),

    /**
     * SOS
     */
    SOS(2, "SOS");

    /**
     * code
     */
    private Integer code;

    /**
     * 描述
     */
    private String desc;

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

    NoticeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NoticeTypeEnum parse(Integer code) {
        for (NoticeTypeEnum item : NoticeTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum UpDownEnum {
    /**
     * 上架
     */
    UP_SHELVES(1, "上架"),

    /**
     * 下架
     */
    DOWN_SHELVES(0, "下架");

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

    UpDownEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UpDownEnum parse(String code) {
        for (UpDownEnum item : UpDownEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

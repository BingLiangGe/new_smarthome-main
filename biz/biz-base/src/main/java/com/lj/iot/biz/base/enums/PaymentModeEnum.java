package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum PaymentModeEnum {
    /**
     * 套餐
     */
    PACKAGE(1, "套餐"),

    /**
     * 自定
     */
    SELFDETERMINED(2, "自定"),

    /**
     * 套餐+自定义
     */
    ALL(3, "套餐+自定义");

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

    PaymentModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaymentModeEnum parse(String code) {
        for (PaymentModeEnum item : PaymentModeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

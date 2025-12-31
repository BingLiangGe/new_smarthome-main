package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum HotelOrderStatusEnum {
    /**
     * 待付款
     */
    UNPAY(1, "待付款"),
    /**
     * 已付款
     */
    HASPAY(2, "已付款"),


    USEING(3, "已付款"),

    OVER(4, "已付款"),

    /**
     * 已取消
     */
    CANCEL(5, "已取消");
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

    HotelOrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HotelOrderStatusEnum parse(String code) {
        for (HotelOrderStatusEnum item : HotelOrderStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

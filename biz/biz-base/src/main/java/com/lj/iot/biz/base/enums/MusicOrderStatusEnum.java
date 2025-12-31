package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum MusicOrderStatusEnum {
    /**
     * 待付款
     */
    UN_PAY(1, "待付款"),

    /**
     * 已付款
     */
    SUCCESS(2, "已付款"),
    /**
     * 已取消
     */
    CANCEL(3, "已取消");
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

    MusicOrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MusicOrderStatusEnum parse(String code) {
        for (MusicOrderStatusEnum item : MusicOrderStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum CouponStatusEnum {
    /**
     * 未使用
     */
    UN_USEING(0, "未使用"),

    /**
     * 已使用
     */
    HAS_USEING(1, "已使用"),

    /**
     * 已过期
     */
    EXPIRE_TIME(2, "已过期");

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

    CouponStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CouponStatusEnum parse(String code) {
        for (CouponStatusEnum item : CouponStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

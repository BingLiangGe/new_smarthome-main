package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum HotelRoomStatusEnum {
    /**
     * 未使用
     */
    FREE(1, "空闲"),

    /**
     * 已使用
     */
    USEING(2, "使用中");

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

    HotelRoomStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static HotelRoomStatusEnum parse(String code) {
        for (HotelRoomStatusEnum item : HotelRoomStatusEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

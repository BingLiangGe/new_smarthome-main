package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2023/5/25
 * @since 1.0.0
 */
public enum RoomTypeEnum {
    /**
     * 钟点房
     */
    HOURROOM(1, "钟点房"),

    /**
     * 包夜
     */
    BAOYE(2, "包夜");

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

    RoomTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoomTypeEnum parse(String code) {
        for (RoomTypeEnum item : RoomTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.base.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 性别枚举类
 */
public enum GenderEnum {
    /**
     * 未知
     */
    UNKNOWN(0, "未知"),
    /**
     * 男性
     */
    MAN(1, "男性"),

    /**
     * 女性
     */
    WOMAN(2, "女性");

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

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static GenderEnum parse(String code) {
        for (GenderEnum item : GenderEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static List<Integer> getGenderValues(){
        List<Integer> codeList = new ArrayList<>();
        for (GenderEnum item : GenderEnum.values()) {
            codeList.add(item.getCode());
        }
        return codeList;
    }
}

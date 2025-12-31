package com.lj.iot.biz.base.enums;

/**
 * 操作枚举类
 */
public enum OperationTypeEnum {
    /**
     * 新增
     */
    ADD_OPERATION("ADD", "新增"),

    /**
     * 更新
     */
    UPDATE_OPERATION("UPDATE", "更新");

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

    OperationTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationTypeEnum parse(String code) {
        for (OperationTypeEnum item : OperationTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.base.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum AccountTypeEnum {

    /**
     * 酒店账号
     */
    HOTEL("0", "酒店账号"),

    /**
     * 主账号
     */
    MASTER("1", "主账号"),

    /**
     * 成员账号
     */
    MEMBER("3", "成员账号"),

    /**
     * 酒店子账号
     */
    HOTEL_SUB("01", "酒店成员账号"),

    /**
     * 酒店子账号[有过期时间]
     */
    HOTEL_SUB_TEMP("02", "临时子账号"),

    /**
     * 子账号
     */
    SUB_EDIT("11", "可编辑子账号"),

    /**
     * 不可编辑子账号
     */
    SUB_UN_EDIT("12", "不可编辑子账号");

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

    AccountTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AccountTypeEnum parse(String code) {
        for (AccountTypeEnum item : AccountTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

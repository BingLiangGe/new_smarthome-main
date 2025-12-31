package com.lj.iot.biz.service.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum FileTypeEnum {
    /**
     * 填写完整格式
     * 0-0-0-0-0-1
     */
    SIX_N("1", "填写完整格式"),

    /**
     * 第六项统一填写 X
     * 0-0-0-0-0-X
     */
    SIX_X("2", "第六项统一填写 X"),

    /**
     * 无需传值，只能按照 keyid 传值
     */
    KEY_3("3", "无需传值，只能按照 keyid 传值"),

    /**
     * 无需传值，只能按照 keyid 传值
     */
    KEY_6("6", "无需传值，只能按照 keyid 传值");
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

    FileTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FileTypeEnum parse(String code) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

package com.lj.iot.biz.service.enums;

/**
 * @author mz
 * @Date 2022/7/25
 * @since 1.0.0
 */
public enum OfflineTypeEnum {

    /**
     * 离线添加删除
     */
    OFFLINE_DELETE("0", "离线添加删除"),

    /**
     * 离线编辑推送
     */
    OFFLINE_EDIT("1", "离线修改推送"),

    /**
     * 触发场景
     */
    OFFLINE_SCENE_TRIGGER("1", "触发场景"),

    /**
     * 离线添加推送
     */
    OFFLINE_ADD("2", "离线添加推送");


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

    OfflineTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OfflineTypeEnum parse(String code) {
        for (OfflineTypeEnum item : OfflineTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}

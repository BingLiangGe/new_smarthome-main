package com.lj.iot.biz.base.enums;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ^_^
 * @date 2022/8/25 11:06
 */
public enum DynamicEntitiesNameEnum {
    /**
     * 场景语料
     */
    SceneCorpus("new_scene"),

    /**
     * 设备名称
     */
    DeviceName("dn"),

    /**
     * 设备名称
     */
    IFlytek_DEVICE_NAME("smartH_deviceAlias"),
    /**
     * 房间名称
     */
    RoomName("new_room_name"),


    Goods("new_goods"),

    /**
     * 模式
     */
    Model("new_model");

    DynamicEntitiesNameEnum(String code) {
        this.code = code;
    }

    /**
     * 名字
     */
    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static Set<String> allNames() {
        Set<String> set = new HashSet<>();
        for (DynamicEntitiesNameEnum value : DynamicEntitiesNameEnum.values()) {
            set.add(value.getCode());
        }
        return set;
    }
}

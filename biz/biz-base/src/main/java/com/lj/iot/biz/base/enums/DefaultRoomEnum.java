package com.lj.iot.biz.base.enums;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ^_^
 * @date 2022/8/25 11:06
 */
public enum DefaultRoomEnum {
    /**
     * 客厅
     */
    LIVING_ROOM("客厅"),
    DINING_ROOM("餐厅"),
    BED_ROOM("卧室"),
    COOK_ROOM("厨房");

    DefaultRoomEnum(String name) {
        this.name = name;
    }

    /**
     * 名字
     */
    private String name;

    private String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public static List<String> allNames() {
        List<String> list = new ArrayList<>();
        for (DefaultRoomEnum value : DefaultRoomEnum.values()) {
            list.add(value.name);
        }
        return list;
    }
}

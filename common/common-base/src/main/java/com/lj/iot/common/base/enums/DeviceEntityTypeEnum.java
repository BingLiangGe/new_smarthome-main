package com.lj.iot.common.base.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 麻将thimodel
 */
public enum DeviceEntityTypeEnum {

    LIGHT("light", "灯类"),
    AIRCONTROL("airControl", "空调类"),
    SOCKET("socket", "插座类"),
    curtain("curtain", "窗类"),
    MESH_LOCK("mesh_lock", "门锁类"),
    GATE_LOCK("gate_lock", "门锁类"),
    ROOM_LOCK("room_lock", "门锁类"),
    GATWAY("gatway", "主控类"),
    MAHJONG_MACHINE("mahjong_machine", "麻将机类"),
    SMART_WATCH("smart_watch", "手表类"),
    SMOKING_LIGHTS("smoking_lights", "净化器类"),
    NEW_SMOKING_LIGHTS("new_smoking_lights", "灯类"),
    FAN("fan", "风扇类"),
    TV("tv", "电视类"),
    PROJECTOR("projector", "投影仪类"),
    AIRCLEANER("airCleaner", "空气净化器类"),
    SETTOPBOX("setTopBox", "机顶盒类"),
    RACKS("racks", "晾衣架类");


    private String code;
    private String name;


    DeviceEntityTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.lj.iot.common.base.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 取暖桌
 */
public enum HeatingTableEnum {

    POWERSTATE("powerstate", "开关", new HashMap() {{
        put(1, "");
        put(0, "");
    }}, "A5,02,05,03,A1,01,A2"),
    ISOTHERMAL_MODEL("isothermal_model", "恒温模式开关", new HashMap() {{
        put(1, "");
        put(0, "");
    }}, "A5,02,05,03,A2,01,A3"),
    FULL_CONTROL_BUTTON("full_control_button", "全控按键", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,A3,01,A4"),
    WARM_GEAR("warm_gear", "保暖档位", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B1,01,B2"),
    TABLE_UP("table_up", "桌面上升", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B2,01,B3"),
    TABLE_STOP("table_stop", "桌面停止", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B2,00,B2"),
    TABLE_DOWN("table_down", "桌面下降", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B2,02,B4"),
    MACHINE_STOP("machine_stop", "整机停止", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B3,00,B3"),
    MACHINE_UP("machine_up", "整机上升", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B3,01,B4"),
    MACHINE_DOWN("machine_down", "整机下降", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,B3,02,B5"),
    TASK_TIME("task_time", "定时时间", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D1,01,D2"),
    LEFT_FRONT("left_front", "左前取暖", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D2,01,D3"),
    RIGHT_FRONT("right_front", "右前取暖", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D3,01,D4"),
    LEFT_REAR("left_rear", "左后取暖", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D4,01,D5"),
    right_rear("right_rear", "右后取暖", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D5,01,D6"),
    heng_diao("heng_diao", "亨调档位", new HashMap() {{
        put(0, "");
    }}, "A5,02,05,03,D6,01,D7"),
    connection("connection", "连接", new HashMap() {{
        put(0, "");
    }}, "A5,02,0A,02,02,02,00");

    private String code;
    private String name;

    private Map<Integer, String> values;

    private String commend;

    HeatingTableEnum(String code, String name, Map<Integer, String> values, String commend) {
        this.code = code;
        this.name = name;
        this.values = values;
        this.commend = commend;
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

    public Map<Integer, String> getValues() {
        return values;
    }

    public void setValues(Map<Integer, String> values) {
        this.values = values;
    }

    public String getCommend() {
        return commend;
    }

    public void setCommend(String commend) {
        this.commend = commend;
    }
}

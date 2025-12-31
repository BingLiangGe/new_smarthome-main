package com.lj.iot.common.base.enums;

import com.lj.iot.common.base.dto.MahjongMachineVoiceDto;

import java.util.HashMap;
import java.util.Map;

/**
 * 麻将_声控thimodel
 */
public enum MahjongMachineVoiceEnum {

    OPERATION("operation", "操作盘升降", new HashMap() {{
        put(0,MahjongMachineVoiceDto.builder()
                .value("0")
                .code("55,05,00,00,00,00").build());
    }}),
    POSITION("position", "档位", new HashMap() {{
        put(1, MahjongMachineVoiceDto.builder()
                .value("18")
                .code("55,01,00,00,00,1").build());
        put(2, MahjongMachineVoiceDto.builder()
                .value("160")
                .code("55,01,00,00,00,2").build());
        put(3, MahjongMachineVoiceDto.builder()
                .value("152")
                .code("55,01,00,00,00,3").build());
        put(4, MahjongMachineVoiceDto.builder()
                .value("148")
                .code("55,01,00,00,00,4").build());
        put(5, MahjongMachineVoiceDto.builder()
                .value("144")
                .code("55,01,00,00,00,5").build());
        put(6, MahjongMachineVoiceDto.builder()
                .value("142")
                .code("55,01,00,00,00,6").build());
        put(7, MahjongMachineVoiceDto.builder()
                .value("140")
                .code("55,01,00,00,00,7").build());
        put(8, MahjongMachineVoiceDto.builder()
                .value("138")
                .code("55,01,00,00,00,8").build());
        put(9, MahjongMachineVoiceDto.builder()
                .value("136")
                .code("55,01,00,00,00,9").build());
        put(10, MahjongMachineVoiceDto.builder()
                .value("132")
                .code("55,01,00,00,00,a").build());
        put(11, MahjongMachineVoiceDto.builder()
                .value("128")
                .code("55,01,00,00,00,b").build());
        put(12, MahjongMachineVoiceDto.builder()
                .value("126")
                .code("55,01,00,00,00,c").build());
        put(13, MahjongMachineVoiceDto.builder()
                .value("124")
                .code("55,01,00,00,00,d").build());
        put(14, MahjongMachineVoiceDto.builder()
                .value("120")
                .code("55,01,00,00,00,e").build());
        put(15, MahjongMachineVoiceDto.builder()
                .value("116")
                .code("55,01,00,00,00,f").build());
        put(16, MahjongMachineVoiceDto.builder()
                .value("112")
                .code("55,01,00,00,00,10").build());
        put(17, MahjongMachineVoiceDto.builder()
                .value("110")
                .code("55,01,00,00,00,11").build());
        put(18, MahjongMachineVoiceDto.builder()
                .value("108")
                .code("55,01,00,00,00,12").build());
        put(19,  MahjongMachineVoiceDto.builder()
                .value("108")
                .code("55,01,00,00,00,13").build());
        put(20,  MahjongMachineVoiceDto.builder()
                .value("104")
                .code("55,01,00,00,00,14").build());
        put(21, MahjongMachineVoiceDto.builder()
                .value("102")
                .code("55,01,00,00,00,15").build());
        put(22, MahjongMachineVoiceDto.builder()
                .value("102")
                .code("55,01,00,00,00,16").build());
        put(23, MahjongMachineVoiceDto.builder()
                .value("100")
                .code("55,01,00,00,00,17").build());
        put(24, MahjongMachineVoiceDto.builder()
                .value("96")
                .code("55,01,00,00,00,18").build());
        put(25, MahjongMachineVoiceDto.builder()
                .value("96")
                .code("55,01,00,00,00,19").build());
        put(26, MahjongMachineVoiceDto.builder()
                .value("92")
                .code("55,01,00,00,00,1a").build());
        put(27,MahjongMachineVoiceDto.builder()
                .value("88")
                .code("55,01,00,00,00,1b").build());
        put(28,MahjongMachineVoiceDto.builder()
                .value("84")
                .code("55,01,00,00,00,1c").build());
        put(29,MahjongMachineVoiceDto.builder()
                .value("84")
                .code("55,01,00,00,00,1d").build());
        put(30, MahjongMachineVoiceDto.builder()
                .value("84")
                .code("55,01,00,00,00,1e").build());
        put(31, MahjongMachineVoiceDto.builder()
                .value("80")
                .code("55,01,00,00,00,1f").build());
        put(32, MahjongMachineVoiceDto.builder()
                .value("72")
                .code("55,01,00,00,00,20").build());
        put(33, MahjongMachineVoiceDto.builder()
                .value("72")
                .code("55,01,00,00,00,21").build());
        put(34,MahjongMachineVoiceDto.builder()
                .value("54")
                .code("55,01,00,00,00,22").build());
        put(35, MahjongMachineVoiceDto.builder()
                .value("40")
                .code("55,01,00,00,00,23").build());
        put(36, MahjongMachineVoiceDto.builder()
                .value("36")
                .code("55,01,00,00,00,24").build());
        put(37,  MahjongMachineVoiceDto.builder()
                .value("36")
                .code("55,01,00,00,00,25").build());
        put(38,  MahjongMachineVoiceDto.builder()
                .value("32")
                .code("55,01,00,00,00,26").build());
        put(39,  MahjongMachineVoiceDto.builder()
                .value("137")
                .code("55,01,00,00,00,28").build());
    }});


    private String code;
    private String name;

    private Map<Integer, MahjongMachineVoiceDto> values;

    MahjongMachineVoiceEnum(String code, String name, Map<Integer, MahjongMachineVoiceDto> values) {
        this.code = code;
        this.name = name;
        this.values = values;
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

    public Map<Integer, MahjongMachineVoiceDto> getValues() {
        return values;
    }

    public void setValues(Map<Integer, MahjongMachineVoiceDto> values) {
        this.values = values;
    }
}

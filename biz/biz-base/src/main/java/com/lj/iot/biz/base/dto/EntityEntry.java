package com.lj.iot.biz.base.dto;

import lombok.Data;

@Data
public class EntityEntry {
    /**
     * 用于匹配iflyos词条
     */
    private String entryKey;

    /**
     * 对应的词条别名
     */
    private String entryName;

    /**
     * 值
     */
    private Object value;

    /**
     * 模式;=:不变;+:调高;-:调低
     */
    private String mode = "=";
}

package com.lj.iot.biz.base.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IntentConfigDto {

    private static final long serialVersionUID = 1L;


    /**
     * 主键,自动生成
     */
    private Long id;

    /**
     * 意图名称;和AIUI对应
     */
    private String intentName;

    /**
     * 支持设备类型（多个用,隔开。为空表示支持全部设备）
     */
    private String supportDeviceType;

    /**
     * 不支持设备（多个用,隔开，为空表示全部支持）
     */
    private String opposeDeviceType;

    /**
     * 属性标识
     */
    private String identifier;

    /**
     * 回复语料;#val#从iflyos辅助槽位取值
     */
    private String replyTxt;

    /**
     * 技能实体Id
     */
    private Long skillEntityId;

    /**
     * 是否有deviceName槽位(1:是;0:否),默认否
     */
    private Boolean isDeviceNameSlot;

    /**
     * 是否有RoomName槽位(1:是;0:否),默认否
     */
    private Boolean isRoomNameSlot;



    /**
     * 是否有iflyos官方value槽位(1:是;0:否),默认否
     */
    private Boolean isValueSlot;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}


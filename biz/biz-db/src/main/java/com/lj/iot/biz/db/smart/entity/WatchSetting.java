package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@Builder
@Getter
@Setter
@TableName("watch_setting")
public class WatchSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "setting_id", type = IdType.AUTO)
    private Integer settingId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 设置值
     */
    private String settingValue;

    /**
     * 设置类型 0数据上传时间设置  1数据间隔报警设置
     */
    private Integer settingType;

    /**
     * 数据类型 0血压 1血氧 2心率
     */
    private Integer dataType;


    private Integer valueType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

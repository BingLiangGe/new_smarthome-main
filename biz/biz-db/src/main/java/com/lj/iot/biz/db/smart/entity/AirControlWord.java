package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-04-11
 */
@Getter
@Setter
@TableName("air_control_word")
@Builder
public class AirControlWord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 拼音
     */
    private String pinyin;

    /**
     * 按键值
     */
    private String keyCode;

    /**
     * 度数
     */
    private String value;

    /**
     * 中文语句
     */
    private String cnName;

    /**
     * 中文类型
     */
    private String deviceType;
}

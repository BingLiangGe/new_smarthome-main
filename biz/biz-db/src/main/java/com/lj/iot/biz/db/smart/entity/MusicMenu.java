package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 *
 * 音乐表
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("music_menu")
public class MusicMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 音乐名称
     */
    private String musicName;

    /**
     * 音乐地址URL
     */
    private String musicUrl;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //当前选择
    private boolean isDefaultMusic;

    //设备ID
    private String deviceId;

    //音乐ID
    private String musicId;

    //音乐ID
    private Integer status;

    //音乐ID
    @TableField(exist=false)
    private String volume;

}

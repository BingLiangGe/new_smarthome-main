package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2023-11-01
 */
@Getter
@Setter
@TableName("bioradar_log")
public class BioradarLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "bioradar_id", type = IdType.AUTO)
    private Integer bioradarId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 状态 0无人 1有人
     */
    private Integer bioradarStatus;

    /**
     * 距离
     */
    private Integer bioradarNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

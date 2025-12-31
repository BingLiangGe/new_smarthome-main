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
 * 手表sos记录
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@Builder
@Getter
@Setter
@TableName("watch_sos")
public class WatchSos implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "sos_id", type = IdType.AUTO)
    private Integer sosId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 拨打的电话号
     */
    private String sosMobile;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


    private Integer sosType;
}

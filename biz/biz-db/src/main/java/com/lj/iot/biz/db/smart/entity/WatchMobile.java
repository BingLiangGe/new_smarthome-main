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
 * @since 2023-09-12
 */
@Builder
@Getter
@Setter
@TableName("watch_mobile")
public class WatchMobile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "wm_id", type = IdType.AUTO)
    private Integer wmId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 手机号
     */
    private String wmMobile;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 手表健康数据
 * </p>
 *
 * @author tyj
 * @since 2023-10-25
 */
@Builder
@Getter
@Setter
@TableName("watch_health")
public class WatchHealth implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "health_id", type = IdType.AUTO)
    private Integer healthId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 健康数据值
     */
    private String healthValue;

    /**
     * 类型 0血压 1血氧 2心率
     */
    private Integer healthType;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;
}

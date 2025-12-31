package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2024-03-14
 */
@Builder
@Data
@TableName("device_record")
public class DeviceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 添加时间
     */
    private LocalDateTime createTime;

    /**
     * 添加用户
     */
    private String userId;
}

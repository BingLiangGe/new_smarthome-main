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
 * @since 2024-02-19
 */
@Getter
@Setter
@TableName("user_device_node")
@Builder
public class UserDeviceNode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "node_id", type = IdType.AUTO)
    private Integer nodeId;

    /**
     * 序号
     */
    private String serialNumber;

    /**
     * 地址
     */
    private String address;

    /**
     * 主控设备号
     */
    private String masterDeviceId;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 节点子元素
     */
    private String subelement;

    private String deviceId;

    private String netkey;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

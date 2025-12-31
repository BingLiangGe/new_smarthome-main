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
 * @since 2023-10-23
 */
@Builder
@Getter
@Setter
@TableName("fence_setting")
public class FenceSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "fence_id", type = IdType.AUTO)
    private Integer fenceId;

    /**
     * 围栏描述
     */
    private String fenceName;

    /**
     * 所属用户
     */
    private String userId;

    /**
     * 经度
     */
    private String fenceLng;

    /**
     * 纬度
     */
    private String fenceLat;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

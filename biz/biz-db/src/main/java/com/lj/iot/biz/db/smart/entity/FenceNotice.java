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
@TableName("fence_notice")
public class FenceNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer noticeId;

    /**
     * 设备id
     */
    private String deviceId;

    private String lat;

    private String lng;

    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-02-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("app_upgrade")
public class AppUpgrade implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 下载地址
     */
    private String url;

    /**
     * 版本码
     */
    private long versionCode;

    /**
     * 软件版本号
     */
    private String version;

    /**
     * 描述
     */
    private String details;

    /**
     * 描述
     */
    private int type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 场景时间调度表
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("scene_schedule")
public class SceneSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 是否开启
     */
    private Boolean enable;

    /**
     * 时间条件
     */
    private String cron;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

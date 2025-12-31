package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.data.annotation.Transient;

/**
 * <p>
 * 情景表
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Scene  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 家Id
     */
    private Long homeId;

    /**
     * 主控Id
     */
    private String masterId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景图片
     */
    private String sceneIcon;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 口令
     */
    private String command;

    /**
     * 最近一次执行时间
     */
    private LocalDateTime lastExecutionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     *  实时表关联字段
     */
    @TableField(exist = false)
    private String cron;


    /**
     *  是否默认0为非默认 1默认
     */

    private Integer isDefault;
}

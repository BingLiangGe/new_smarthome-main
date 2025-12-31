package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 情景模板
 * </p>
 *
 * @author xm
 * @since 2023-03-08
 */
@Getter
@Setter
@TableName("scene_template")
public class SceneTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 情景条件列表
     */
    private String conditions;

    /**
     * 情景名称
     */
    private String name;

    /**
     * 场景类型 0:一键执行;1:自动化
     */
    private Boolean type;

    /**
     * 场景背景图
     */
    private String backgroundUrl;
}

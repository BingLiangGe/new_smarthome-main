package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 实体词条
 * </p>
 *
 * @author xm
 * @since 2022-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "skill_entity_entry",autoResultMap = true)
public class SkillEntityEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 意图名
     */
    private String intentName;

    /**
     * 实体标识
     */
    private String entityKey;

    /**
     * 词条
     */
    private String entryKey;

    /**
     * 词条名
     */
    private String entryName;

    /**
     * 物模型对象
     */
    @TableField(typeHandler = FastjsonTypeHandler.class, javaType = true)
    private ThingModelProperty thingModelProperty;

    /**
     * 按键
     */
    private String keyCode;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 产品物模型属性按键定义表
 * </p>
 *
 * @author xm
 * @since 2022-09-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("product_thing_model_key")
public class ProductThingModelKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 产品Id
     */
    private String productId;

    private Long modelId;

    private Integer delay;

    private Integer time;

    /**
     * 按键名称
     */
    private String keyName;

    /**
     * 按键下标
     */
    private Integer keyIdx;

    /**
     * 属性标识
     */
    private String identifier;

    /**
     * 按键代码
     */
    private String keyCode;

    /**
     * 模式;=:不变;+:调高;-:调低
     */
    private String mode;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String codeData;
}

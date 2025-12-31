package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 产品分裂虚设备表
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "product_invented",autoResultMap = true)
public class ProductInvented implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 是否在场景中显示
     */
    private Boolean isShowScene;

    /**
     * 物理模型
     */
    @TableField(typeHandler = FastjsonTypeHandler.class,javaType = true)
    private ThingModel thingModel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

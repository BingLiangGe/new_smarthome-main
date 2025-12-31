package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 射频设备品牌表
 * </p>
 *
 * @author xm
 * @since 2022-09-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rf_brand")
public class RfBrand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 品牌名称
     */
    private String brandName;
}

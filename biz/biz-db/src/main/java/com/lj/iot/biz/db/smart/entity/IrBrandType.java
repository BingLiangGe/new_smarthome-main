package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author xm
 * @since 2022-09-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ir_brand_type")
public class IrBrandType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备类型ID
     */
    private Long deviceTypeId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 产品名称
     */
    private String brandName;

    /**
     * 首字母
     */
    private String firstLetter;
}

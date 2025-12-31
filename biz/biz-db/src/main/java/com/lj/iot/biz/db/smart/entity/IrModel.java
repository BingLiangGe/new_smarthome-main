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
@TableName("ir_model")
public class IrModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型ID
     */
    private Long deviceTypeId;

    /**
     * 品牌ID
     */
    private Long brandId;

    /**
     * 品牌名
     */
    private String brandName;

    /**
     * 型号名
     */
    private String modeName;

    /**
     * 文件编号
     */
    private String fileId;

    /**
     * 文件类型
     */
    private String fileType;
}

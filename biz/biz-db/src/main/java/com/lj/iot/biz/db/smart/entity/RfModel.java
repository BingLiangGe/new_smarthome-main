package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 * 射频设备型号表
 * </p>
 *
 * @author xm
 * @since 2022-09-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rf_model")
public class RfModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备类型ID
     */
    private Long deviceTypeId;

    /**
     * 设备品牌Id
     */
    private Long brandId;

    /**
     * 品牌名
     */
    private String brandName;

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 头数据（整型数组）
     */
    private String headData;

    /**
     * 编码类型
     */
    private String codeType;

    /**
     * 开始0长时间（微秒）
     */
    private Integer startZeroTime;

    /**
     * 单位时间（微秒）
     */
    private Integer unitTime;

    /**
     * 发送组数
     */
    private Integer sentCount;
}

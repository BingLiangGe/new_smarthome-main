package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 
 * 音乐产品表
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("music_product")
public class MusicProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 卡的数量
     */
    private Integer count;

    /**
     * 产品封面
     */
    private String coverUrl;

    /**
     * 产品名称
     */
    private String musicName;

    /**
     * 单价;单位元,保留两位小数
     */
    private BigDecimal price;
}

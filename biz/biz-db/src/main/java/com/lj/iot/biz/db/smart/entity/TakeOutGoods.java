package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 
 * 外卖商品表
 * 
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("take_out_goods")
public class TakeOutGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建人Id
     */
    private String createdBy;

    /**
     * 是否删除(1:是;0:否),默认否
     */
    private Boolean isDel;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 更新人Id
     */
    private String updatedBy;

    /**
     * 商品别名
     */
    private String goodsAlias;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品状态;1:上架;2:下架
     */
    private Integer state;

    /**
     * 用户Id
     */
    private String userId;
}

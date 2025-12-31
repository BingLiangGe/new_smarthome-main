package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 外卖商品表
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_goods")
public class UserGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private String userId;

    private Long hotelId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品别名
     */
    private String goodsAlias;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品状态;1:上架;0:下架
     */
    private Boolean state;

    /**
     * 商品代码
     */
    private String goodsCode;

    /**
     * 商品单位
     */
    private String unit;


    private String images;
}

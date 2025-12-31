package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;

/**
 *
 * 轮播图
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Banner implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 点击行为
     */
    private String action;

    /**
     * 图片url
     */
    private String img;

    /**
     * 标题
     */
    private String title;

    /**
     * 目标uri
     */
    private String uri;

    /**
     * 排序
     */
    private Integer weight;
}

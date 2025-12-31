package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author tyj
 * @since 2023-05-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("j_dat")
public class JDat implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 文件编号
     */
    private String tags;

    /**
     * 索引
     */
    private String dats;


    @TableField(exist = false)
    private String identifier;


    @TableField(exist = false)
    private String keyCode;
}

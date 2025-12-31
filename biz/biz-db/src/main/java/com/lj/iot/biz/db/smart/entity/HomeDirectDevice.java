package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.*;

/**
 * <p>
 * 直连设备实体
 * </p>
 *
 * @author xm
 * @since 2022-12-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("home_direct_device")
public class HomeDirectDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 家ID
     */
    private Long homeId;

    /**
     * 设备数据
     */
    private String data;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
}

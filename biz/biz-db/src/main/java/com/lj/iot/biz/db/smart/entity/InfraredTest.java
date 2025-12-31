package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 红外码测试
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("infrared_test")
public class InfraredTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设备名称
     */
    private Integer brandId;

    /**
     * 升级路径
     */
    private String brandName;

    /**
     * 软件版本号
     */
    private String commd;

    /**
     * 是否升级成功 0否 1是
     */
    private String tags;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * <p>
 * 参数配置表
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("parameter_config")
public class ParameterConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典key
     */
    private String dictionaryKey;

    /**
     * 字典值
     */
    private String dictionaryValue;

    /**
     * 备注
     */
    private String dictionaryRemark;

    /**
     * creater
     */
    private String creater;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}

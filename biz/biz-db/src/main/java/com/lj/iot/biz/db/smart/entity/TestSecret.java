package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author tyj
 * @since 2023-08-02
 */
@Getter
@Setter
@Builder
@TableName("test_CCCFDF")
public class TestCCCFDF implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 绑定人
     */
    private String userId;

    /**
     * 三元组
     */
    private String CCCFDF;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private String deviceId;
}

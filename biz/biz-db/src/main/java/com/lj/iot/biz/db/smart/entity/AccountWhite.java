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
 * @since 2023-11-18
 */
@Builder
@Getter
@Setter
@TableName("account_white")
public class AccountWhite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "white_id", type = IdType.AUTO)
    private Integer whiteId;

    /**
     * 账号
     */
    private String mobile;

    /**
     * 描述
     */
    private LocalDateTime createTime;
}

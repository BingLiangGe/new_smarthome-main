package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2023-02-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedDot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *消息中心红点
     */
    private int systemMessagesType;

    /**
     * 网关升级红点
     */
    private int gatewayUpgradeType;



}

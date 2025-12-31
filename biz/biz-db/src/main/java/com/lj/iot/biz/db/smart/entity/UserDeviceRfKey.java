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
 * <p>
 * 用户射频设备按键码值表
 * </p>
 *
 * @author xm
 * @since 2022-08-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_device_rf_key")
public class UserDeviceRfKey implements Serializable {

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

    /**
     * 设备Id
     */
    private String deviceId;

    /**
     * 设备型号按键Id
     */
    private Long keyId;

    /**
     * 按键下标
     */
    private Integer keyIdx;


    /**
     * 设备按键code
     */
    private String keyCode;

    /**
     * 设备按键名
     */
    private String keyName;

    /**
     * 设备型号Id
     */
    private Long modelId;

    /**
     * 码值（整型数组）
     */
    private String codeData;

    /**
     * 是否有效(1:是;0:否),默认否
     */
    private Boolean isEffective;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    //private String identifier;
}

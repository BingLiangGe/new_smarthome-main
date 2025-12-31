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
 * ota 升级记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("upgrade_record")
public class UpgradeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备名称
     */
    private String deviceId;

    /**
     * 升级路径
     */
    private String filePath;

    /**
     * 软件版本号
     */
    private String softWareVersion;

    /**
     * 是否升级成功 0否 1是
     */
    private Integer isSuccess;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime successTime;

    /**
     * 硬件版本号
     */
    private String hardWareVersion;

    /**
     * 下发次数
     */
    private Integer successCount;
}

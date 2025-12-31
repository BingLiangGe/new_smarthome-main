package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xm
 * @since 2022-09-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("rf_device_type")
public class RfDeviceType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 设备名
     */
    private String deviceName;

    /**
     * 选中
     */
    @TableField(exist = false)
    private boolean check;

    @TableField(exist = false)
    private List<Long> brandIds;

    @TableField(exist = false)
    private List<String> brandNames;
}

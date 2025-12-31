package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.*;

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
@TableName("ir_device_type")
public class IrDeviceType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 设备名
     */
    private String deviceName;
}

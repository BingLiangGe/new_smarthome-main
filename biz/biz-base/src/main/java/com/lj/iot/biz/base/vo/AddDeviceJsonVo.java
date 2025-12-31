package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author mz
 * @Date 2022/8/4
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddDeviceJsonVo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 设备ID
     */
    private String id;
    /**
     * 设备秘钥
     */
    private String CCCFDF;
    /**
     * 盐
     */
    private String productId;
}

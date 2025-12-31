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
public class ExportDeviceJsonVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    private String user_id;
    /**
     * 设备秘钥
     */
    private String password_hash;
    /**
     * 盐
     */
    private String salt = "";
    /**
     * 是否超级用户
     */
    private Boolean is_superuser = false;
}

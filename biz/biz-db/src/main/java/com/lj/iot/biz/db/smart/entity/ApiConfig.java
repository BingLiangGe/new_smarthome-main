package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 第三方推送地址配置
 * </p>
 *
 * @author tyj
 * @since 2023-7-10 15:27:34
 */
@Getter
@Setter
@TableName("api_config")
public class ApiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "api_id", type = IdType.AUTO)
    private Integer apiId;

    /**
     * 开房时间
     */
    private String apiUrl;

    /**
     * 退房时间
     */
    private String apiDesc;


}

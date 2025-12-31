package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import lombok.*;

/**
 *
 * 组播表
 *
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Multicast implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 主控ID
     */
    private String masterId;

    /**
     * 组播码
     */
    private Long multicastCode;

    /**
     * 设备主键ID
     */
    private Long userDeviceId;

    /**
     * 控制类型码
     */
    private Integer modeId;

    /**
     * 属性值(0：打开，1：关闭)
     */
    private Integer propValue;

    /**
     * 元素索引
     */
    private Integer meshElementIndex;

    /**
     * 主控key
     */
    private String masterKey;

    /**
     * 0,生效1，失效
     */
    private String flag;
}

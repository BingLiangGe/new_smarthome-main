package com.lj.iot.biz.db.smart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 *
 * 空间,家,房子表
 * 
 *
 * @author xm
 * @since 2022-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Home implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键,自动生成
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id(主账号)
     */
    private String userId;

    /**
     * 家庭类型
     */
    private String homeType;

    /**
     * 家名称
     */
    private String homeName;

    /**
     * 是否是默认的家(1:是;0:否),默认否
     */
    private Boolean isDefaultHome;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 国家名称
     */
    private String country;


    /**
     * 省名称
     */
    private String province;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 区域名称
     */
    private String district;

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 经度
     */
    private BigDecimal lng;


    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 酒店id
     */
    private Long hotelId;
}

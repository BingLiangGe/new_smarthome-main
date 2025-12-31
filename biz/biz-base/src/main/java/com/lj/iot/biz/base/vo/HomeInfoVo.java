package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * homeId
     */
    private Long homeId;

    /**
     * 管理员
     */
    private Boolean isMain;

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

}

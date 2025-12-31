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
public class HomePageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;


    /**
     * 详细地址
     */
    private String address;

    /**
     * 城市名称
     */
    private String city;

    /**
     * 国家名称
     */
    private String country;

    /**
     * 是否是默认的家(1:是;0:否),默认否
     */
    private Boolean isDefaultHome;

    /**
     * 区域名称
     */
    private String district;


    /**
     * 家名称
     */
    private String homeName;

    /**
     * 纬度
     */
    private BigDecimal lat;

    /**
     * 经度
     */
    private BigDecimal lng;

    /**
     * 省名称
     */
    private String province;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 用户Id(主账号)
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户手机号
     */
    private String mobile;
}

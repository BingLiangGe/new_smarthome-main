package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAddDto {

    /**
     * 家庭名称
     */
    @NotBlank(message = "家庭不能为空")
    private String homeName;

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
     * 酒店id
     */
    private Long hotelId;
}

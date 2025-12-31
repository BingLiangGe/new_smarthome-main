package com.lj.iot.biz.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeEditDto {

    /**
     * Home ID
     */
    @NotNull(message = "家ID不能为空")
    private Long homeId;

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
     * 备注
     */
    private String remarks;
}

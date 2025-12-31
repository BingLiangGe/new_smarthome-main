package com.lj.iot.biz.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HomeDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 创建人userId
     */
    private String createdBy;

    /**
     * 修改日期
     */
    private LocalDateTime updateDate;

    /**
     * 修改人
     */
    private String updatedBy;

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
    @NotNull(message ="是否为默认家必填")
    private Boolean isDefaultHome;

    /**
     * 区域名称
     */
    private String district;

    /**
     * 家名称
     */
    @NotNull(message ="家名称为必填")
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
     * 用户ID
     */
    @JsonIgnore
    private String userId;
}

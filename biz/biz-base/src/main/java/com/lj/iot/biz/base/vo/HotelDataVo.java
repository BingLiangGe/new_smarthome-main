package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID   酒店成员关系表ID
     */
    private Long id;

    /**
     * 酒店
     */
    private Long hotelId;

    /**
     * 酒店成员ID
     */
    private String memberUserId;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 是否是默认的酒店(1:是;0:否),默认否
     */
    private Boolean isDefault;

    /**
     * 酒店创建者
     */
    private Boolean isMain;
}

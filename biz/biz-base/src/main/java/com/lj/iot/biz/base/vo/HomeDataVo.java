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
public class HomeDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 家ID
     */
    private Long homeId;

    /**
     * 家用户ID
     */
    private Long homeUserId;

    /**
     * 家名称
     */
    private String homeName;

    /**
     * 是否是默认的家(1:是;0:否),默认否
     */
    private Boolean isDefaultHome;
    /**
     * 是否是主账户家庭
     */
    private int isMain;
}

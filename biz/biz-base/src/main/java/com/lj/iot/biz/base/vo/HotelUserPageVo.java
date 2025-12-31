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
public class HotelUserPageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String nickname;
    private String mobile;
    private Long roleId;
}

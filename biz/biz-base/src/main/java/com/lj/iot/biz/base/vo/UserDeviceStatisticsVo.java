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
public class UserDeviceStatisticsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer online=0;
    private Integer offline=0;
}

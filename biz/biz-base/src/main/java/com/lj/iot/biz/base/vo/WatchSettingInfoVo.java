package com.lj.iot.biz.base.vo;

import com.lj.iot.common.base.constant.CodeConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchSettingInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String timeValue;

    private String highData;

    private String lowData;


}

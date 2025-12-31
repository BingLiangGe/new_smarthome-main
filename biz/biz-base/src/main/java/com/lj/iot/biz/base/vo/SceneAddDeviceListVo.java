package com.lj.iot.biz.base.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneAddDeviceListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标志
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 设备列表
     */
    private List<UserDeviceVo> userDeviceList;
}

package com.lj.iot.biz.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 手表联系人
 * @author tyj
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchMobileDto {

    /**
     * 设备 ID
     */
    @NotNull(message = "设备id不能为空")
    private String deviceId;

    /**
     * 手机号
     */
    @NotNull(message = "手机号不能为空")
    private String mobiles;

}

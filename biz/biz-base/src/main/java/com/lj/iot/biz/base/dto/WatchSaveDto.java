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
public class WatchSaveDto {

    /**
     * 设备 ID
     */
    @NotNull(message = "设备id 不能为空")
    private String deviceId;

    /**
     * 房间 id
     */
    @NotNull(message = "房间不能为空")
    private String homeId;

}

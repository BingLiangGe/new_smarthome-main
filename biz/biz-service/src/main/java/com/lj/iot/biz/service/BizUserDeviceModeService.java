package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.UserDeviceModeDto;
import com.lj.iot.biz.db.smart.entity.UserDeviceMode;

public interface BizUserDeviceModeService {
    /**
     * 编辑自定义模式
     * @param dto
     * @param uId
     */
    UserDeviceMode edit(UserDeviceModeDto dto, String uId);
}

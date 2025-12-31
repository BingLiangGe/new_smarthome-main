package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.DeviceNotificationDto;
import com.lj.iot.common.aiui.core.dto.IntentDto;

/**
 * 3326通知
 */
public interface DeviceNotificationService {

    void handle(DeviceNotificationDto deviceNotificationDto);
}

package com.lj.iot.watchnetty.handle;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;

import java.util.Map;

public interface TcpHandle {

    String handle(UserDevice userDevice, Map<String, String> dataMap);
}

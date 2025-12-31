package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.aiui.core.vo.IntentResult;

public interface IntentService {
    void handle(UserDevice masterUserDevice,IntentDto intentDto);
}
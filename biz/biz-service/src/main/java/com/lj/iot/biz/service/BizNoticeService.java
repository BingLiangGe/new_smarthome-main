package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;

/**
 *
 */
public interface BizNoticeService {

    void callWaiter(UserDevice masterUserDevice, IntentDto intentDto);

    void order(UserDevice masterUserDevice, IntentDto intentDto);

    void sos(UserDevice masterUserDevice);

    void handle(IdDto dto, String userId);

    void forHelp(UserDevice masterUserDevice, IntentDto intentDto);
}

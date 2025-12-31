package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 呼叫前台
 */
@Component("intentService_callWaiter")
public class IntentServiceCallWaiter implements IntentService {

    @Autowired
    private BizNoticeService bizNoticeService;

    /**
     * 插槽
     * <p>
     * scene
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        bizNoticeService.callWaiter(masterUserDevice, intentDto);
    }
}

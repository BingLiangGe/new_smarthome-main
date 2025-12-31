package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.service.BizNoticeService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 求救服务
 */
@Component("intentService_forHelp")
public class IntentServiceForHelp implements IntentService {

    @Autowired
    private BizNoticeService bizNoticeService;

    /**
     * 插槽
     * <p>
     * forHelp
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        bizNoticeService.forHelp(masterUserDevice, intentDto);
    }
}

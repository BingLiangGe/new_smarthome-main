package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能技能处理
 */
@Component("intentService_default")
public class IntentServiceDefault implements IntentService {

    @Autowired
    private IntentCommonHandler intentCommonHandler;

    /**
     * 插槽
     * <p>
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = intentCommonHandler.buildHandleData(masterUserDevice, intentDto);
        intentCommonHandler.doSend(handleUserDeviceDtoList, OperationEnum.AI_C);
    }
}

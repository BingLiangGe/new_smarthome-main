package com.lj.iot.biz.service.aiui;

import cn.hutool.extra.spring.SpringUtil;
import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备开关
 */
@Component("intentService_deviceSwitch")
public class IntentServiceDeviceSwitch implements IntentService {

    @Autowired
    private IntentCommonHandler intentCommonHandler;

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
        List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList = intentCommonHandler.buildHandleData(masterUserDevice, intentDto);
        intentCommonHandler.doSend(handleUserDeviceDtoList, OperationEnum.AI_C);
    }

}

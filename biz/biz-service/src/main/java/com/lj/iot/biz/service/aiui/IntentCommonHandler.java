package com.lj.iot.biz.service.aiui;

import com.lj.iot.biz.base.dto.HandleUserDeviceDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.UserDeviceFilterVo;
import com.lj.iot.biz.db.smart.entity.SkillEntityEntry;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.aiui.core.dto.IntentDto;

import java.util.List;

/**
 * 技能公共处理接口
 */
public interface IntentCommonHandler {


    /**
     * 过滤 deviceName   roomName   all   产品类型
     *
     * @param masterUserDevice
     * @param intentDto
     * @return
     */
    List<UserDeviceFilterVo> filter(UserDevice masterUserDevice, IntentDto intentDto, String productTypes);

    List<HandleUserDeviceDto<UserDevice>> buildHandleData(UserDevice masterUserDevice, IntentDto intentDto);

    void doSend(List<HandleUserDeviceDto<UserDevice>> handleUserDeviceDtoList, OperationEnum operationEnum);
}

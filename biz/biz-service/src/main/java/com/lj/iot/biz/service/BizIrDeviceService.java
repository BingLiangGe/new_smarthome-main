package com.lj.iot.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.lj.iot.biz.base.dto.TestIrDataDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.dto.ThingModel;

/**
 * 红外设备业务类
 */
public interface BizIrDeviceService {


    void sendIrData(UserDevice userDevice, ThingModel changeThingModel, String keyCode);


    void testIrData(TestIrDataDto dto, String userId);

    /**
     * 发送红外码扩展参数
     *
     * @param topProductType
     * @param changeThingModel
     * @return
     */
    JSONObject extendData(String topProductType, ThingModel changeThingModel);
}

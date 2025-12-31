package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.MasterDeviceDto;
import com.lj.iot.biz.base.vo.SceneAddDeviceListVo;
import com.lj.iot.biz.base.vo.UserDeviceBindVo;
import com.lj.iot.biz.base.vo.UserDeviceStatisticsVo;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.common.mqtt.client.core.HandleMessage;

import java.util.List;
import java.util.Map;

/**
 * 用户设备按键
 */
public interface BizUserDeviceRfKeyService {

    List<UserDeviceRfKey> OfflineList(String masterDeviceId, String deviceId);
}

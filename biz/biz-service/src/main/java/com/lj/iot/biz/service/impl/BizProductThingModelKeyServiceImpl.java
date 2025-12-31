package com.lj.iot.biz.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.DeviceDto;
import com.lj.iot.biz.base.enums.SignalEnum;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.mapper.ProductThingModelKeyMapper;
import com.lj.iot.biz.db.smart.service.IProductThingModelKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizProductThingModelKeyService;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BizProductThingModelKeyServiceImpl implements BizProductThingModelKeyService {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IProductThingModelKeyService productThingModelKeyService;

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;



    @Override
    public List<ProductThingModelKey> keyList(DeviceDto dto) {

        if (StringUtils.isNotEmpty(dto.getDeviceId())) {
            UserDevice userDevice = userDeviceService.getById(dto.getDeviceId());
            ValidUtils.isNullThrow(userDevice, "数据不存在");
            List<ProductThingModelKey> productThingModelKeyList = productThingModelKeyService.keyList(userDevice.getProductId(), userDevice.getModelId());

            //射频设备需要获取codeData
            if (userDevice.getSignalType().equals(SignalEnum.RF.getCode())) {
                List<UserDeviceRfKey> userDeviceRfKeyList = userDeviceRfKeyService.list(new QueryWrapper<>(UserDeviceRfKey.builder()
                        .deviceId(dto.getDeviceId())
                        .build()));
                for (UserDeviceRfKey userDeviceRfKey : userDeviceRfKeyList) {
                    for (ProductThingModelKey productThingModelKey : productThingModelKeyList) {
                        if (productThingModelKey.getKeyCode().equals(userDeviceRfKey.getKeyCode())) {
                            productThingModelKey.setKeyCode(userDeviceRfKey.getKeyCode());
                            break;
                        }
                    }
                }
            }
            return productThingModelKeyList;
        }

        if (StringUtils.isEmpty(dto.getProductId())) {
            throw CommonException.FAILURE("产品ID不能为空");
        }

        return productThingModelKeyService.keyList(dto.getProductId(), dto.getModelId() == null ? 0L : dto.getModelId());
    }

    @Override
    public ProductThingModelKey getProductThingModelKey(UserDevice userDevice, String keyCode) {
        ProductThingModelKey productThingModelKey = productThingModelKeyService.getProductThingModelKey(userDevice.getProductId(), userDevice.getModelId(), keyCode);
        log.error("getProductThingModelKey,deviceId={},productId={},modelId={},keyCode={},productThingModelKey ={}",
                userDevice.getDeviceId(), userDevice.getProductId(), userDevice.getModelId(), keyCode, JSONObject.toJSONString(productThingModelKey));
        ValidUtils.isNullThrow(productThingModelKey, "按键不存在");
        return productThingModelKey;
    }
}

package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.DeviceDto;
import com.lj.iot.biz.base.vo.ProductThingModelKeyVo;
import com.lj.iot.biz.db.smart.entity.ProductThingModelKey;
import com.lj.iot.biz.db.smart.entity.UserDevice;

import java.util.List;

public interface BizProductThingModelKeyService {



    List<ProductThingModelKey> keyList(DeviceDto dto);

    ProductThingModelKey getProductThingModelKey(UserDevice userDevice, String keyCode);



}

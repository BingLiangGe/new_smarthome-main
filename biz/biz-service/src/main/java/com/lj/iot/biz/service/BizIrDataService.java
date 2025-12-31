package com.lj.iot.biz.service;

import com.lj.iot.biz.db.smart.entity.IrData;
import com.lj.iot.biz.db.smart.entity.IrModel;
import com.lj.iot.common.base.dto.ThingModel;

import java.util.List;

public interface BizIrDataService {


    List<IrData> getDjAirContData(String fileIds);

    List<IrModel> getDjAirContr();

    String getIrData(String productType, IrModel irModel, ThingModel thingModel, Integer keyIndex);

    String getDataIndex(String productType, IrModel irModel, ThingModel thingModel, Integer keyIndex);

    String syncData(String fileId, String dataIndex);
}

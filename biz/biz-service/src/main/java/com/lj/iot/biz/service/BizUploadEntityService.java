package com.lj.iot.biz.service;

import com.lj.iot.biz.base.enums.DynamicEntitiesNameEnum;

import java.util.Set;

public interface BizUploadEntityService {

    void uploadEntityUserLevel(String userId, DynamicEntitiesNameEnum nameEnum);

    void uploadDeviceNameUserLevel(String userId, Set<String> productTypeSet);

    void uploadEntityAppLevel(DynamicEntitiesNameEnum nameEnum);

    void uploadEntityAppLevel(DynamicEntitiesNameEnum nameEnum, String type);
}

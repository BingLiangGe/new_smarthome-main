package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.SaveRfDataDto;
import com.lj.iot.biz.base.dto.StudyRfData2Dto;
import com.lj.iot.biz.base.dto.StudyRfDataDto;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.dto.ThingModel;

public interface BizRfDeviceService {

    /**
     * 射频学码
     *
     * @param studyRfDataDto
     */
    void learnRfData(StudyRfDataDto studyRfDataDto, String userId);

    void learnRfData(StudyRfData2Dto dto, String userId);

    /**
     * 保存射频码
     *
     * @param dto
     */
    void saveRfData(SaveRfDataDto dto, String userId);


    void sendRfData(UserDevice userDevice, ThingModel changeThingModel, String keyCode);
}

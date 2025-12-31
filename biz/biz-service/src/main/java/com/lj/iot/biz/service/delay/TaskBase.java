package com.lj.iot.biz.service.delay;

import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.dto.ThingModel;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TaskBase {

    private String identifier;

    private UserDevice userDevice;

    private ThingModel changeThingModel;

    private String keyCode;

    private OperationEnum operationEnum;

    public TaskBase(UserDevice userDevice, ThingModel changeThingModel, String keyCode, OperationEnum operationEnum) {
        this.userDevice=userDevice;
        this.changeThingModel=changeThingModel;
        this.keyCode=keyCode;
        this.operationEnum=operationEnum;
    }

    public TaskBase() {
    }
}

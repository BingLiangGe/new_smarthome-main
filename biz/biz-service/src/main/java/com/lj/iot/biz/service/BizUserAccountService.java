package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.SubTempAccountAddDto;
import com.lj.iot.biz.base.dto.SubTempAccountEditDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.common.base.vo.CommonResultVo;

public interface BizUserAccountService {

    CommonResultVo<String> sendTokenComment();

    void cancellation(String userId);

    UserAccount addDeviceUserAccount(UserDevice userDevice);

    UserAccount addTempUserAccount(SubTempAccountAddDto dto, String userId);

    UserAccount freshTokenTempUserAccount(SubTempAccountEditDto dto, String actualUserId);

    UserAccount editDeviceUserAccount(UserDevice userDevice, Boolean flag);
}

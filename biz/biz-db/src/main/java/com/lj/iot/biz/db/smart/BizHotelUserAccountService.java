package com.lj.iot.biz.db.smart;

import com.lj.iot.biz.base.dto.LoginDto;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;

public interface BizHotelUserAccountService {

    HotelUserAccount register(LoginDto loginDto);
}

package com.lj.iot.common.system.service;

import com.lj.iot.common.system.entity.SysUser;

public interface ISysPasswordService {

    void check(SysUser user, String password);
}

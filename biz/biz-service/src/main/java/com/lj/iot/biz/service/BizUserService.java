package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;

/**
 *
 */
public interface BizUserService {

    IPage<UserAccount> customPage(PageDto pageDto);

}

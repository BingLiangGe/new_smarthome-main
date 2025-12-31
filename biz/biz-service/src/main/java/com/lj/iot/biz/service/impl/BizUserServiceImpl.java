package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.service.BizUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BizUserServiceImpl implements BizUserService {

    @Autowired
    private IUserAccountService userAccountService;

    @Override
    public IPage<UserAccount> customPage(PageDto pageDto) {
        return userAccountService.customPage(pageDto);
    }
}

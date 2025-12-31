package com.lj.iot.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.dto.IdPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.db.smart.service.ISosContactService;
import com.lj.iot.biz.service.BizSosContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@Service
public class BizSosContactServiceImpl implements BizSosContactService {

    @Autowired
    private ISosContactService sosContactService;

    @Override
    public IPage<SosContact> customPage(HomeUserPageDto pageDto) {
        return sosContactService.customPage(pageDto);
    }
}

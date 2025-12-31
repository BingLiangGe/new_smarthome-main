package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.dto.IdPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;

/**
 * 通讯录
 */
public interface BizSosContactService {

    IPage<SosContact> customPage(HomeUserPageDto pageDto);

}

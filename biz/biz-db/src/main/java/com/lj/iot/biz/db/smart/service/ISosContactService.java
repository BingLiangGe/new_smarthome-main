package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.base.dto.IdPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;

/**
 * 紧急呼叫联系人 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface ISosContactService extends IService<SosContact> {

    IPage<SosContact> customPage(HomeUserPageDto pageDto);
}

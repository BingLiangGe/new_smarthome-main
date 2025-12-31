package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.db.smart.mapper.SosContactMapper;
import com.lj.iot.biz.db.smart.service.ISosContactService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * 
 * 紧急呼叫联系人 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class SosContactServiceImpl extends ServiceImpl<SosContactMapper, SosContact> implements ISosContactService {

    @Override
    public IPage<SosContact> customPage(HomeUserPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto),pageDto);
    }
}

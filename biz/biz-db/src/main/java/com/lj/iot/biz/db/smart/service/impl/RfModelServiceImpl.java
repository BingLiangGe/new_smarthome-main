package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.lj.iot.biz.db.smart.mapper.RfModelMapper;
import com.lj.iot.biz.db.smart.service.IRfModelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * 
 * 射频设备型号表 服务实现类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class RfModelServiceImpl extends ServiceImpl<RfModelMapper, RfModel> implements IRfModelService {

    @Override
    public IPage<RfModel> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto),pageDto);
    }
}

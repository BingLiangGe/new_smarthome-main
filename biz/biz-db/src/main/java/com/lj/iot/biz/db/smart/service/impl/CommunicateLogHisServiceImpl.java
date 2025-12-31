package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.CommunicateLogHis;
import com.lj.iot.biz.db.smart.mapper.CommunicateLogHisMapper;
import com.lj.iot.biz.db.smart.service.ICommunicateLogHisService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * 历史通话记录 服务实现类
 *
 * @author xm
 * @since 2022-07-19
 */
@DS("smart")
@Service
public class CommunicateLogHisServiceImpl extends ServiceImpl<CommunicateLogHisMapper, CommunicateLogHis> implements ICommunicateLogHisService {

    @Override
    public IPage<CommunicateLogHis> customPage(PageDto pageDto, Long homeId, String userId) {
        return this.page(PageUtil.page(pageDto), new QueryWrapper<>(CommunicateLogHis.builder()
                .homeId(homeId)
                .userId(userId)
                .build()));
    }
}

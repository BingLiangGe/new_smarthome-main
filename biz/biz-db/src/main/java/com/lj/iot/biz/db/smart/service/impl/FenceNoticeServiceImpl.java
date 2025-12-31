package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.FenceNotice;
import com.lj.iot.biz.db.smart.mapper.FenceNoticeMapper;
import com.lj.iot.biz.db.smart.service.IFenceNoticeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-23
 */
@DS("smart")
@Service
public class FenceNoticeServiceImpl extends ServiceImpl<FenceNoticeMapper, FenceNotice> implements IFenceNoticeService {

    @Resource
    private FenceNoticeMapper mapper;

    @Override
    public Integer selectTodayNumber(String deviceId,Integer type) {
        return mapper.selectTodayNumber(deviceId,type);
    }
}

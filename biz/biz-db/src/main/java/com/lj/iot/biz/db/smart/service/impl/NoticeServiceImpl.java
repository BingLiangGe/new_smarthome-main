package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.NoticePageDto;
import com.lj.iot.biz.db.smart.entity.Notice;
import com.lj.iot.biz.db.smart.mapper.NoticeMapper;
import com.lj.iot.biz.db.smart.service.INoticeService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 外卖订单表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
@DS("smart")
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Override
    public IPage<Notice> customPage(NoticePageDto pageDto, Long hotelId, String userId) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto, hotelId, userId);
    }

    @Override
    public Long unHandle(NoticePageDto pageDto, Long hotelId, String userId) {
        return this.baseMapper.unHandle(pageDto, hotelId, userId);
    }
}

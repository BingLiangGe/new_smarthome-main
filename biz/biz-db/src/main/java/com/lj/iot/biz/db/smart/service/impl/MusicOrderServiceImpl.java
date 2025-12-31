package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.MusicOrderPageDto;
import com.lj.iot.biz.base.vo.MusicOrderPageVo;
import com.lj.iot.biz.db.smart.entity.MusicOrder;
import com.lj.iot.biz.db.smart.mapper.MusicOrderMapper;
import com.lj.iot.biz.db.smart.service.IMusicOrderService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * 
 * 音乐畅听卡订单表 服务实现类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class MusicOrderServiceImpl extends ServiceImpl<MusicOrderMapper, MusicOrder> implements IMusicOrderService {

    @Override
    public IPage<MusicOrderPageVo> customPage(MusicOrderPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }
}

package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.lj.iot.biz.db.smart.mapper.MusicProductMapper;
import com.lj.iot.biz.db.smart.service.IMusicProductService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * 音乐产品表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class MusicProductServiceImpl extends ServiceImpl<MusicProductMapper, MusicProduct> implements IMusicProductService {
    @Override
    public IPage<MusicProduct> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }
}

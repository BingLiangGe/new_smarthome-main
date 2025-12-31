package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.EntityEntryPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntityEntry;
import com.lj.iot.biz.db.smart.mapper.SkillEntityEntryMapper;
import com.lj.iot.biz.db.smart.service.ISkillEntityEntryService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 实体词条 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-06
 */
@DS("smart")
@Service
public class SkillEntityEntryServiceImpl extends ServiceImpl<SkillEntityEntryMapper, SkillEntityEntry> implements ISkillEntityEntryService {

    @Override
    public IPage<SkillEntityEntry> customPage(EntityEntryPageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }
}

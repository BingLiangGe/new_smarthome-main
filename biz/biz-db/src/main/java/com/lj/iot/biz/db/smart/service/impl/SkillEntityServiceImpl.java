package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntity;
import com.lj.iot.biz.db.smart.mapper.SkillEntityMapper;
import com.lj.iot.biz.db.smart.service.ISkillEntityService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 技能实体表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class SkillEntityServiceImpl extends ServiceImpl<SkillEntityMapper, SkillEntity> implements ISkillEntityService {

    @Override
    public IPage<SkillEntity> customPage(PageDto pageDto) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto);
    }

    @Override
    public SkillEntity getByIntentName(String intentName) {
        return this.getOne(new QueryWrapper<>(SkillEntity.builder()
                .intentName(intentName)
                .build()));
    }

    @CacheEvict(value = "skill_entity", key = "#entityKey")
    @Override
    public void edit(String entityKey, SkillEntity skillEntity) {

    }
}

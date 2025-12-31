package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntity;

/**
 * 技能实体表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface ISkillEntityService extends IService<SkillEntity> {

    IPage<SkillEntity> customPage(PageDto pageDto);

    SkillEntity getByIntentName(String intentName);

    void edit(String entityKey, SkillEntity skillEntity);
}

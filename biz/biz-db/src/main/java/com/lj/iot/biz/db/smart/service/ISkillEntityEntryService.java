package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.EntityEntryPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntityEntry;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 实体词条 服务类
 * </p>
 *
 * @author xm
 * @since 2022-09-06
 */
public interface ISkillEntityEntryService extends IService<SkillEntityEntry> {

    IPage<SkillEntityEntry> customPage(EntityEntryPageDto pageDto);

}

package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.EntityEntryPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntityEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 实体词条 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-06
 */
public interface SkillEntityEntryMapper extends BaseMapper<SkillEntityEntry> {

    IPage<SkillEntityEntry> customPage(IPage<SkillEntityEntry> page, @Param("params") EntityEntryPageDto pageDto);

}

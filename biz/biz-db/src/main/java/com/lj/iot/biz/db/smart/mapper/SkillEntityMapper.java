package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.SkillEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 技能实体表 Mapper 接口
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface SkillEntityMapper extends BaseMapper<SkillEntity> {

    IPage<SkillEntity> customPage(IPage<SkillEntity> page, @Param("params") PageDto pageDto);
}

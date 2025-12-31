package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.MusicProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * 音乐产品表 Mapper 接口
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface MusicProductMapper extends BaseMapper<MusicProduct> {

    IPage<MusicProduct> customPage(IPage<MusicProduct> page, @Param("params") PageDto pageDto);
}

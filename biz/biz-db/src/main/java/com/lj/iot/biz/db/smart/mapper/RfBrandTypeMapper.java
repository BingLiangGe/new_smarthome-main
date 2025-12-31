package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.RfBrandType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 射频产品和设备类型关联表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-15
 */
public interface RfBrandTypeMapper extends BaseMapper<RfBrandType> {

    IPage<RfBrandType> customPage(IPage<RfBrandType> page, @Param("params") PageDto pageDto);

}

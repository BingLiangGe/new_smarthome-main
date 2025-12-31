package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ParameterConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 参数配置表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-27
 */
public interface ParameterConfigMapper extends BaseMapper<ParameterConfig> {

    List<ParameterConfig> customPage(IPage<ParameterConfig> page, @Param("params") PageDto paramDto);

}

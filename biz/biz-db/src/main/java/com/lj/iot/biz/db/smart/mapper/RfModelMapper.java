package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.lj.iot.biz.db.smart.entity.RfModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.base.dto.PageDto;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 射频设备型号表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface RfModelMapper extends BaseMapper<RfModel> {

    IPage<RfModel> customPage(IPage<RfModel> page, @Param("params") PageDto pageDto);

}

package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductInvented;
import com.lj.iot.biz.db.smart.entity.ProductType;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 产品分裂虚设备表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
public interface ProductInventedMapper extends BaseMapper<ProductInvented> {

    IPage<ProductInvented> customPage(IPage<ProductType> page, @Param("params") ProductIdPageDto pageDto);
}

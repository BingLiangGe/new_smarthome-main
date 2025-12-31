package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductIdPageDto;
import com.lj.iot.biz.db.smart.entity.ProductMode;
import com.lj.iot.biz.db.smart.entity.ProductType;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 产品模式 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-29
 */
public interface ProductModeMapper extends BaseMapper<ProductMode> {

    IPage<ProductMode> customPage(IPage<ProductType> page, @Param("params") ProductIdPageDto pageDto);

}

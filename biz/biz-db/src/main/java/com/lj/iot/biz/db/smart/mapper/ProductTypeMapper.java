package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.ProductType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 领捷产品类别表 Mapper 接口
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
public interface ProductTypeMapper extends BaseMapper<ProductType> {

    IPage<ProductType> customPage(IPage<ProductType> page, @Param("params") PageDto pageDto);

    List<String> subTypeList(@Param("productTypeRay") String productTypeRay);
}

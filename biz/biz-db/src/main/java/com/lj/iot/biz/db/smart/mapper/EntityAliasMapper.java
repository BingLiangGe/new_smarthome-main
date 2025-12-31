package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.EntityAliasPageDto;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.EntityAlias;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.Product;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-11-07
 */
public interface EntityAliasMapper extends BaseMapper<EntityAlias> {

    IPage<EntityAlias> customPage(IPage<EntityAlias> page,@Param("params")EntityAliasPageDto pageDto);

}

package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.base.vo.BrandTypeVo;
import com.lj.iot.biz.db.smart.entity.IrBrandType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-09-20
 */
public interface IrBrandTypeMapper extends BaseMapper<IrBrandType> {

    List<BrandTypeVo> listByDeviceTypeId(@Param("deviceTypeId") Long deviceTypeId);
}

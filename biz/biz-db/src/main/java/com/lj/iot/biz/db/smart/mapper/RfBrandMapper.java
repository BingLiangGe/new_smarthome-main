package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.RfBrand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 射频设备品牌表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface RfBrandMapper extends BaseMapper<RfBrand> {

    List<RfBrand> findBrandListByTypeId(@Param(value = "rfDeviceTypeId") Long rfDeviceTypeId);
}

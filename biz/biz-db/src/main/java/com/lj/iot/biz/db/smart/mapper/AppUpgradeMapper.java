package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2023-02-23
 */
public interface AppUpgradeMapper extends BaseMapper<AppUpgrade> {

    IPage<AppUpgrade> newPage(IPage<AppUpgrade> pageDto, @Param("params") AppUpgrade pDto);

    IPage<AppUpgrade> customPage(IPage<AppUpgrade> page);

    AppUpgrade findByVersionCode(@Param("versionCode")long versionCode, @Param("type") Integer type);

    AppUpgrade findMasterControlUrl(@Param("versionCode")long versionCode);
}

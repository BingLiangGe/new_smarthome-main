package com.lj.iot.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.ProductPageDto;
import com.lj.iot.biz.base.dto.ProductUpgradeAddDto;
import com.lj.iot.biz.base.dto.ProductUpgradeEditDto;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.lj.iot.biz.db.smart.entity.ProductUpgrade;
import com.lj.iot.common.base.dto.PageDto;

/**
 * APP升级
 *
 * @author mz
 * @Date 2022/7/21
 * @since 1.0.0
 */
public interface BizAppUpgradeService {

    IPage<AppUpgrade> customPage(PageDto pageDto);

    void add(AppUpgrade paramDto);

    void edit(AppUpgrade paramDto);

    void delete(AppUpgrade paramDto);

    AppUpgrade findByVersionCode(long versionCode,Integer type);

    AppUpgrade findMasterControlUrl(long versionCode);
}

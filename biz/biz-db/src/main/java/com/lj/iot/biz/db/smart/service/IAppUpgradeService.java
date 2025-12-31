package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xm
 * @since 2023-02-23
 */
public interface IAppUpgradeService extends IService<AppUpgrade> {

    IPage<AppUpgrade> customPage(IPage<AppUpgrade> page);

    AppUpgrade findByVersionCode(long versionCode,Integer type);

    AppUpgrade findMasterControlUrl(long versionCode);
}
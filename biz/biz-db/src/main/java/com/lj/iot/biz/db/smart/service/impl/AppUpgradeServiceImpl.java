package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.db.smart.entity.AppUpgrade;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.mapper.AppUpgradeMapper;
import com.lj.iot.biz.db.smart.service.IAppUpgradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-02-23
 */
@DS("smart")
@Service
public class AppUpgradeServiceImpl extends ServiceImpl<AppUpgradeMapper, AppUpgrade> implements IAppUpgradeService {

    @Override
    public IPage<AppUpgrade> customPage(IPage<AppUpgrade> page) {
        return this.baseMapper.customPage(page);
    }

    @Override
    public AppUpgrade findByVersionCode(long versionCode,Integer type) {
        return this.baseMapper.findByVersionCode(versionCode,type);
    }

    @Override
    public AppUpgrade findMasterControlUrl(long versionCode) {
        return this.baseMapper.findMasterControlUrl(versionCode);
    }
}

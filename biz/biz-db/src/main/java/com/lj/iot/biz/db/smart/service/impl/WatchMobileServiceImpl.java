package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.WatchMobile;
import com.lj.iot.biz.db.smart.mapper.WatchMobileMapper;
import com.lj.iot.biz.db.smart.service.IWatchMobileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-09-12
 */
@DS("smart")
@Service
public class WatchMobileServiceImpl extends ServiceImpl<WatchMobileMapper, WatchMobile> implements IWatchMobileService {

}

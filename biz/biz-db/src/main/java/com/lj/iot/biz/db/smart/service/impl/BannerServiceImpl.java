package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.Banner;
import com.lj.iot.biz.db.smart.mapper.BannerMapper;
import com.lj.iot.biz.db.smart.service.IBannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 轮播图 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

}

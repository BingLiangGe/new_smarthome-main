package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.db.smart.mapper.HomeMapper;
import com.lj.iot.biz.db.smart.service.IHomeService;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 *
 * 空间,家,房子表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class HomeServiceImpl extends ServiceImpl<HomeMapper, Home> implements IHomeService {

    @Override
    public HomeDataVo queryHomeDataList(String userId) {
        return this.baseMapper.queryHomeDataList(userId);
    }

    @Override
    public IPage<HomePageVo> customPage(HomeRoomPageDto pageDto) {
        IPage<HomePageVo> page = PageUtil.page(pageDto);
        return this.baseMapper.customPage(page, pageDto);
    }

    @Override
    public Home findHomeByHomeIdAndUserId(Long homeId, String userId) {
        return getOne(new QueryWrapper<>(Home.builder()
                .id(homeId)
                .userId(userId).build()));
    }
}

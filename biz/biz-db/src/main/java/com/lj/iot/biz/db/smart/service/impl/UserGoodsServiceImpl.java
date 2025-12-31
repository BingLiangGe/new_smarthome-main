package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.vo.SeachDeviceVo;
import com.lj.iot.biz.db.smart.entity.UserGoods;
import com.lj.iot.biz.db.smart.mapper.UserGoodsMapper;
import com.lj.iot.biz.db.smart.service.IUserGoodsService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 外卖商品表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-09-28
 */
@DS("smart")
@Service
public class UserGoodsServiceImpl extends ServiceImpl<UserGoodsMapper, UserGoods> implements IUserGoodsService {

    @Override
    public void cutQuantity(Long id, Integer value) {
        ValidUtils.isFalseThrow(this.baseMapper.cutQuantity(id, value), "没有库存了");
    }

    @Override
    public IPage<UserGoods> customPage(PageDto pageDto, Long hotelId, String userId) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto, hotelId, userId);
    }

    @Override
    public IPage<SeachDeviceVo> customPageUserDevice(PageDto pageDto) {
        return this.baseMapper.customPageUserDevice(PageUtil.page(pageDto), pageDto);
    }
}

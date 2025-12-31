package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.TakeOutGoods;
import com.lj.iot.biz.db.smart.mapper.TakeOutGoodsMapper;
import com.lj.iot.biz.db.smart.service.ITakeOutGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 外卖商品表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class TakeOutGoodsServiceImpl extends ServiceImpl<TakeOutGoodsMapper, TakeOutGoods> implements ITakeOutGoodsService {

}

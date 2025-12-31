package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.TakeOutGoodsAlias;
import com.lj.iot.biz.db.smart.mapper.TakeOutGoodsAliasMapper;
import com.lj.iot.biz.db.smart.service.ITakeOutGoodsAliasService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 外卖商品别名表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class TakeOutGoodsAliasServiceImpl extends ServiceImpl<TakeOutGoodsAliasMapper, TakeOutGoodsAlias> implements ITakeOutGoodsAliasService {

}

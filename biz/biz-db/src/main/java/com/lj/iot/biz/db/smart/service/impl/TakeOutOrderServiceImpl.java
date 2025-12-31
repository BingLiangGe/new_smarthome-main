package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.TakeOutOrder;
import com.lj.iot.biz.db.smart.mapper.TakeOutOrderMapper;
import com.lj.iot.biz.db.smart.service.ITakeOutOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 外卖订单表 服务实现类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class TakeOutOrderServiceImpl extends ServiceImpl<TakeOutOrderMapper, TakeOutOrder> implements ITakeOutOrderService {

}

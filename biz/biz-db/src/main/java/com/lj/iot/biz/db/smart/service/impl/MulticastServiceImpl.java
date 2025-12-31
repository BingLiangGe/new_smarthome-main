package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.Multicast;
import com.lj.iot.biz.db.smart.mapper.MulticastMapper;
import com.lj.iot.biz.db.smart.service.IMulticastService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 组播表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class MulticastServiceImpl extends ServiceImpl<MulticastMapper, Multicast> implements IMulticastService {

}

package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.TripletAccount;
import com.lj.iot.biz.db.smart.mapper.TripletAccountMapper;
import com.lj.iot.biz.db.smart.service.ITripletAccountService;
import org.springframework.stereotype.Service;

@DS("smart")
@Service
public class TripletAccountServiceImpl extends ServiceImpl<TripletAccountMapper, TripletAccount> implements ITripletAccountService {
}

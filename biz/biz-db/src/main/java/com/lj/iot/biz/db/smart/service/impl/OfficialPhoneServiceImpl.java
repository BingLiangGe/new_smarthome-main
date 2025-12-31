package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.OfficialPhone;
import com.lj.iot.biz.db.smart.mapper.OfficialPhoneMapper;
import com.lj.iot.biz.db.smart.service.IOfficialPhoneService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 
 * 官方电话表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class OfficialPhoneServiceImpl extends ServiceImpl<OfficialPhoneMapper, OfficialPhone> implements IOfficialPhoneService {

}

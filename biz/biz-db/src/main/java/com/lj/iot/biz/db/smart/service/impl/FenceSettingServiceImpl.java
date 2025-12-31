package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.FenceSetting;
import com.lj.iot.biz.db.smart.mapper.FenceSettingMapper;
import com.lj.iot.biz.db.smart.service.IFenceSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2023-10-23
 */
@DS("smart")
@Service
public class FenceSettingServiceImpl extends ServiceImpl<FenceSettingMapper, FenceSetting> implements IFenceSettingService {

}

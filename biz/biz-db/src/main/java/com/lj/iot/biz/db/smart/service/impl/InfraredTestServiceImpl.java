package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.InfraredTest;
import com.lj.iot.biz.db.smart.entity.UpgradeRecord;
import com.lj.iot.biz.db.smart.mapper.InfraredTestMapper;
import com.lj.iot.biz.db.smart.mapper.UpgradeRecordMapper;
import com.lj.iot.biz.db.smart.service.IInfraredTestService;
import com.lj.iot.biz.db.smart.service.IUpgradeRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 红外码
 * </p>
 *
 * @author tyj
 */
@DS("smart")
@Service
public class InfraredTestServiceImpl extends ServiceImpl<InfraredTestMapper, InfraredTest> implements IInfraredTestService {

}

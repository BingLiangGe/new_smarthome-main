package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.SceneSchedule;
import com.lj.iot.biz.db.smart.mapper.SceneScheduleMapper;
import com.lj.iot.biz.db.smart.service.ISceneScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 场景时间调度表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
@DS("smart")
@Service
public class SceneScheduleServiceImpl extends ServiceImpl<SceneScheduleMapper, SceneSchedule> implements ISceneScheduleService {

}

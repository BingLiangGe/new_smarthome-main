package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.SceneDevice;
import com.lj.iot.biz.db.smart.mapper.SceneDeviceMapper;
import com.lj.iot.biz.db.smart.service.ISceneDeviceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 场景设备表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
@DS("smart")
@Service
public class SceneDeviceServiceImpl extends ServiceImpl<SceneDeviceMapper, SceneDevice> implements ISceneDeviceService {

    @Override
    public List<SceneDevice> getBySceneId(Long sceneId) {
        return this.baseMapper.listSceneDevice(sceneId);
    }

    @Override
    public Set<String> listDeviceIds(Long sceneId) {
        return this.baseMapper.listDeviceIds(sceneId);
    }


}

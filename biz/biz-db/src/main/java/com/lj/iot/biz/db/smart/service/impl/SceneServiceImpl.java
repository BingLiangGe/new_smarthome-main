package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.mapper.SceneMapper;
import com.lj.iot.biz.db.smart.service.ISceneService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 情景表 服务实现类
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements ISceneService {

    @Cacheable(value = "common-cache", key = "'Scene:'+#sceneId", unless = "#result == null")
    @Override
    public Scene getOneByIdCache(Long sceneId) {
        return this.getById(sceneId);
    }

    @CacheEvict(value = "common-cache", key = "'Scene:'+#sceneId")
    @Override
    public void deleteCacheById(Long sceneId) {

    }

    @Override
    public List<Scene> listByCondition(Long homeId, Long sceneId) {
        //拼接查询条件
        QueryWrapper<Scene> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("home_id", homeId);
        if (null != sceneId) {
            queryWrapper.eq("id", sceneId);
        }
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public Scene findByLastAction(Long homeId) {
        return baseMapper.findByLastAction(homeId);
    }

    @Override
    public List<Scene> authList(Long homeId, String userId) {
        return baseMapper.authList(homeId,userId);
    }

    @Override
    public List<Scene> findInSetMasterId(String deviceId) {
        return baseMapper.findInSetMasterId(deviceId);
    }
}

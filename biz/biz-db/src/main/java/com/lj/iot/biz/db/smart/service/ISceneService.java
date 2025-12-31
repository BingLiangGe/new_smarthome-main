package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.Scene;

import java.util.List;

/**
 * 情景表 服务类
 *
 * @author xm
 * @since 2022-07-13
 */
public interface ISceneService extends IService<Scene> {

    Scene getOneByIdCache(Long sceneId);

    void deleteCacheById(Long sceneId);

    List<Scene> listByCondition(Long homeId, Long sceneId);

    Scene findByLastAction(Long homeId);

    List<Scene> authList(Long homeId, String userId);

    List<Scene> findInSetMasterId(String deviceId);
}

package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.SceneDevice;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 场景设备表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
public interface ISceneDeviceService extends IService<SceneDevice> {

    List<SceneDevice> getBySceneId(Long sceneId);


    Set<String> listDeviceIds(Long sceneId);

}

package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.SceneDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 场景设备表 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-08-28
 */
public interface SceneDeviceMapper extends BaseMapper<SceneDevice> {

    Set<String> listDeviceIds(@Param("sceneId") Long sceneId);

    List<SceneDevice> listSceneDevice(Long sceneId);
}

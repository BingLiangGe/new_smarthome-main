package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.base.vo.AuthSceneVo;
import com.lj.iot.biz.base.vo.SceneDataListVo;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 情景表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface SceneMapper extends BaseMapper<Scene> {

    Long getHomeIdById(@Param(value = "sceneId") Long sceneId);

    Scene findByLastAction(@Param(value = "homeId") Long homeId);

    List<Scene> authList(@Param(value = "homeId") Long homeId,@Param(value = "userId") String userId);

    List<Scene> findInSetMasterId(@Param(value = "deviceId") String deviceId);
}

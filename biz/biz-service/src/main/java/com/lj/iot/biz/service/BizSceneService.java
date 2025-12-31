package com.lj.iot.biz.service;

import com.lj.iot.biz.base.dto.OfflineSceneDto;
import com.lj.iot.biz.base.dto.SceneAddDto;
import com.lj.iot.biz.base.dto.SceneCopyDto;
import com.lj.iot.biz.base.dto.SceneEditDto;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.base.vo.OfflineSceneListVo;
import com.lj.iot.biz.base.vo.SceneDetailVo;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.SceneDevice;
import com.lj.iot.biz.db.smart.entity.SceneSchedule;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 场景业务处理层
 */
public interface BizSceneService {

    /**
     * 列表
     *
     * @param homeId
     * @return
     */
    List<Scene> list(Long homeId);


    List<Scene> list(Long homeId, String userId);

    /**
     * 带权限的场景列表
     */
    List<Scene> authList(Long homeId, String userId);

    /**
     * 添加场景
     *
     * @param dto
     */
    Scene add(SceneAddDto dto, String userId);

    Scene add(SceneAddDto dto, String userId,Boolean uploadEntity);

    /**
     * 修改场景
     *
     * @param dto
     * @param userId
     */
    Scene edit(SceneEditDto dto, String userId);

    Scene edit(SceneEditDto dto, String userId,Boolean uploadEntity);

    /**
     * 删除场景
     *
     * @param sceneId 场景ID
     * @param userId  用户ID
     */
    void deleteScene(Long sceneId, String userId);

    /**
     * 解绑主控
     *
     * @param sceneId 场景ID
     * @param userId  用户ID
     */
    void deleteHomeScene(Long sceneId, String userId);

    /**
     * 通过家ID删除场景
     *
     * @param homeId 家ID
     * @param userId 用户ID
     */
    void deleteSceneByHomeId(Long homeId, String userId);

    /**
     * 触发场景
     *
     * @param sceneId
     */

    void trigger(Long sceneId, OperationEnum operationEnum);

    void triggerThree(Long sceneId, OperationEnum operationEnum);


    /**
     * 触发场景-插卡取电
     * @param sceneId
     * @param operationEnum
     */
    void triggerSceneCard(Long sceneId, OperationEnum operationEnum);

    void trigger(Long sceneId, String userId, OperationEnum operationEnum);

    SceneDetailVo<SceneDevice, SceneSchedule> sceneInfo(Long sceneId);

    SceneDetailVo<SceneDevice, SceneSchedule> sceneInfo(Long sceneId, String userId);

    void copy(SceneCopyDto dto, Long hotelId, String userId);

    List<OfflineSceneListVo> listScene(OfflineSceneDto dto);

    Scene findByLastAction(Long homeId);
}

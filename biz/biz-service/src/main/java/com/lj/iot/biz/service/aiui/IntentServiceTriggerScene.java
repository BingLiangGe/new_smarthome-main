package com.lj.iot.biz.service.aiui;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.enums.OperationEnum;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.ISceneService;
import com.lj.iot.biz.service.BizSceneService;
import com.lj.iot.common.aiui.core.dto.IntentDto;
import com.lj.iot.common.util.ValidUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 触发场景
 */
@Component("intentService_triggerScene")
public class IntentServiceTriggerScene implements IntentService {

    @Autowired
    private ISceneService sceneService;

    @Autowired
    private BizSceneService bizSceneService;

    /**
     * 插槽
     * <p>
     * scene
     *
     * @param intentDto
     * @return
     */
    @Override
    public void handle(UserDevice masterUserDevice, IntentDto intentDto) {

        Map<String, IntentDto.Slot> slots = intentDto.getSlots();

        IntentDto.Slot sceneSlot = slots.get("scene");
        ValidUtils.isNullThrow(sceneSlot, "没有识别场景");

        List<Scene> sceneList = sceneService.list(new QueryWrapper<>(Scene.builder()
                .homeId(masterUserDevice.getHomeId())
                .build()));

        sceneList = sceneList.stream()
                .filter(scene -> scene.getCommand().contains(sceneSlot.getNormValue()))
                .filter(scene -> StringUtils.isBlank(scene.getMasterId()) || scene.getMasterId().contains(masterUserDevice.getDeviceId()))
                .collect(Collectors.toList());

        ValidUtils.listIsEmptyThrow(sceneList, "没有对应场景，或者该主控没有绑定对应场景");

        for (Scene scene : sceneList) {
            bizSceneService.trigger(scene.getId(), OperationEnum.AI_S_C);
        }

    }

}

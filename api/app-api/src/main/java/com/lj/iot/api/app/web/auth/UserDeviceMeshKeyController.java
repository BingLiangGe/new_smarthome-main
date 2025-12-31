package com.lj.iot.api.app.web.auth;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.base.dto.MeshKeyBindSceneDto;
import com.lj.iot.biz.db.smart.entity.Scene;
import com.lj.iot.biz.db.smart.entity.UserDeviceMeshKey;
import com.lj.iot.biz.db.smart.service.ISceneService;
import com.lj.iot.biz.db.smart.service.IUserDeviceMeshKeyService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 蓝牙按键【情景面板】
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/user_device_mesh_key")
public class UserDeviceMeshKeyController {

    @Autowired
    private IUserDeviceMeshKeyService userDeviceMeshKeyService;

    @Autowired
    private ISceneService sceneService;

    /**
     * 绑定场景
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "sceneId", type = HomeAuth.PermType.EDIT)
    @PostMapping("bind")
    public CommonResultVo<String> save(@RequestBody @Valid MeshKeyBindSceneDto dto) {

        UserDeviceMeshKey key = userDeviceMeshKeyService.getOne(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(key, "数据不存在");

        Scene scene = sceneService.getById(dto.getSceneId());
        ValidUtils.isNullThrow(scene, "场景不存在");

        userDeviceMeshKeyService.updateById(UserDeviceMeshKey.builder()
                .id(key.getId())
                .sceneId(dto.getSceneId())
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 解绑场景
     *
     * @param dto
     * @return
     */
    @HomeAuth(type = HomeAuth.PermType.EDIT)
    @PostMapping("un_bind")
    public CommonResultVo<String> save(@RequestBody @Valid IdDto dto) {

        UserDeviceMeshKey key = userDeviceMeshKeyService.getOne(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(key, "数据不存在");

        userDeviceMeshKeyService.update(
                Wrappers.<UserDeviceMeshKey>lambdaUpdate()
                        .set(UserDeviceMeshKey::getSceneId, null)
                        .eq(UserDeviceMeshKey::getId, key.getId()));

        return CommonResultVo.SUCCESS();
    }

    /**
     * 按键数据获取
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "deviceId", type = HomeAuth.PermType.ALL)
    @GetMapping("list")
    public CommonResultVo<List<UserDeviceMeshKey>> list(@Valid DeviceIdDto dto) {
        List<UserDeviceMeshKey> userDeviceRfKeys = userDeviceMeshKeyService.list(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .deviceId(dto.getDeviceId())
                .userId(UserDto.getUser().getActualUserId())
                .build()));


        for (UserDeviceMeshKey userDeviceRfKey : userDeviceRfKeys) {
            if (userDeviceRfKey.getSceneId() != null && userDeviceRfKey.getSceneId() != 0L) {
                Scene scene = sceneService.getById(userDeviceRfKey.getSceneId());
                if (scene != null) {
                    userDeviceRfKey.setKeyName(scene.getSceneName());
                }
            }
        }
        return CommonResultVo.SUCCESS(userDeviceRfKeys);
    }
}


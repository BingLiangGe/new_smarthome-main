package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.api.hotel.aop.CustomPermissions;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 蓝牙按键【情景面板】
 */
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
    @CustomPermissions("user_device_mesh_key:bind")
    @PostMapping("bind")
    public CommonResultVo<String> save(@RequestBody @Valid MeshKeyBindSceneDto dto) {

        UserDeviceMeshKey key = userDeviceMeshKeyService.getOne(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(key, "数据不存在");

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
    @CustomPermissions("user_device_mesh_key:un_bind")
    @PostMapping("un_bind")
    public CommonResultVo<String> save(@RequestBody @Valid IdDto dto) {

        UserDeviceMeshKey key = userDeviceMeshKeyService.getOne(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(key, "数据不存在");

        userDeviceMeshKeyService.updateById(UserDeviceMeshKey.builder()
                .id(key.getId())
                .sceneId(0L)
                .build());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 按键数据获取
     *
     * @param dto
     * @return
     */
    @GetMapping("list")
    public CommonResultVo<List<UserDeviceMeshKey>> list(@Valid DeviceIdDto dto) {
        List<UserDeviceMeshKey> userDeviceRfKeys = userDeviceMeshKeyService.list(new QueryWrapper<>(UserDeviceMeshKey.builder()
                .deviceId(dto.getDeviceId())
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


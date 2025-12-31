package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.db.smart.entity.UserDeviceRfKey;
import com.lj.iot.biz.db.smart.service.IUserDeviceRfKeyService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 射频按键
 */
@RestController
@RequestMapping("/api/auth/user_device_rf_key")
public class UserDeviceRfKeyController {

    @Autowired
    private IUserDeviceRfKeyService userDeviceRfKeyService;

    /* *//**
     * 保存码
     *
     * @param dto
     * @return
     *//*
    @PostMapping("save")
    @CustomPermissions("user_device_rf_key:save")
    public CommonResultVo<String> save(@RequestBody SaveCodeDataDto dto) {
        UserDeviceRfKey userDeviceRfKey = userDeviceRfKeyService.getOne(new QueryWrapper<>(UserDeviceRfKey.builder()
                .id(dto.getId())
                .userId(UserDto.getUser().getUId())
                .build()));
        ValidUtils.isNullThrow(userDeviceRfKey, "数据不存在");

        userDeviceRfKeyService.updateById(UserDeviceRfKey.builder()
                .id(userDeviceRfKey.getId())
                .codeData(dto.getCodeData())
                .build());
        return CommonResultVo.SUCCESS();
    }
*/

    /**
     * 按键数据获取
     *
     * @param dto
     * @return
     */
    @GetMapping("list")
    public CommonResultVo<List<UserDeviceRfKey>> list(DeviceIdDto dto) {
        List<UserDeviceRfKey> userDeviceRfKeys = userDeviceRfKeyService.list(new QueryWrapper<>(UserDeviceRfKey.builder()
                .deviceId(dto.getDeviceId())
                .build()));
        ValidUtils.listIsEmptyThrow(userDeviceRfKeys, "设备按键数据不存在");
        return CommonResultVo.SUCCESS(userDeviceRfKeys);
    }
}


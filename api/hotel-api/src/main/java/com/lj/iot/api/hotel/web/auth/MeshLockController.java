package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.base.dto.MeshLockAddDto;
import com.lj.iot.biz.base.dto.MeshLockEditDto;
import com.lj.iot.biz.db.smart.entity.Product;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IProductService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 蓝牙直连锁
 *
 * @author tyj
 */
@RequestMapping("/api/auth/mesh_lock")
@RestController
public class MeshLockController {

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IProductService productService;

    /**
     * 删除设备
     *
     * @param deviceId
     * @return
     */
    @RequestMapping("/removeDevice")
    public CommonResultVo<String> removeDevice(String deviceId) {
        ValidUtils.isNullThrow(deviceId, "deviceId 必传");

        UserDevice beforDevice = userDeviceService.getById(deviceId);
        ValidUtils.isNullThrow(beforDevice, "设备不存在");

        // 蓝牙锁
        if (!"mesh_lock".equals(beforDevice.getProductType())) {
            return CommonResultVo.FAILURE_MSG("非智能锁设备");
        }

        userDeviceService.removeById(beforDevice.getDeviceId());

        return CommonResultVo.SUCCESS();
    }


    /**
     * 验证包间是否存在设备
     *
     * @param homeId
     * @return
     */
    @GetMapping("/checkHomeExist")
    public CommonResultVo<String> checkHomeExist(String homeId) {
        ValidUtils.isNullThrow(homeId, "homeId 必传");

        if (userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(Long.valueOf(homeId))
                .productType("mesh_lock").build())).isEmpty()) {
            return CommonResultVo.SUCCESS();
        }
        return CommonResultVo.FAILURE_MSG("已存在设备");
    }

    /**
     * 编辑设备
     *
     * @param dto
     * @return
     */
    @RequestMapping("/editDevice")
    public CommonResultVo<String> editDevice(@RequestBody MeshLockEditDto dto) {
        UserDevice beforDevice = userDeviceService.getById(dto.getDeviceId());
        ValidUtils.isNullThrow(beforDevice, "设备不存在");
        userDeviceService.update(UserDevice.builder()
                .customName(dto.getCustomName()).build(), new QueryWrapper<>(UserDevice.builder()
                .deviceId(dto.getDeviceId())
                .customName(dto.getCustomName()).build()));
        return CommonResultVo.SUCCESS();
    }


    /**
     * 新增设备
     *
     * @param meshLockAddDto
     * @return
     */
    @PostMapping("/addDevice")
    public CommonResultVo<String> addDevice(@RequestBody MeshLockAddDto meshLockAddDto) {

        UserDevice beforDevice = userDeviceService.getById(meshLockAddDto.getLockMac());

        ValidUtils.noNullThrow(beforDevice, "设备已被绑定!");

        Product lockProduct = productService.getById("9337723");

        UserDevice userDevice = UserDevice.builder()
                .deviceId(meshLockAddDto.getLockMac())
                .physicalDeviceId(meshLockAddDto.getLockMac())
                .userId(UserDto.getUser().getActualUserId())
                .productId(lockProduct.getProductId())
                .homeId(Long.valueOf(meshLockAddDto.getKeyGroupId()))
                .roomId(Long.valueOf(meshLockAddDto.getKeyGroupId()))
                .signalType(lockProduct.getProductType())
                .realProductType(lockProduct.getProductType())
                .topProductType(lockProduct.getProductType())
                .productType(lockProduct.getProductType())
                .status(true)
                .deviceName(lockProduct.getProductName())
                .customName(lockProduct.getProductName())
                .imagesUrl(lockProduct.getImagesUrl())
                .isShowScene(lockProduct.getIsShowScene())
                .lockCCCFDF(meshLockAddDto.getCCCFDF())
                .lockAuthCode(meshLockAddDto.getAuthCode())
                .thingModel(lockProduct.getThingModel()).build();

        //添加设备
        userDeviceService.save(userDevice);

        return CommonResultVo.SUCCESS(userDevice.getDeviceId());
    }


    /**
     * 查询蓝牙智能设备列表-通过包间id
     *
     * @param homeId
     * @return
     */
    @RequestMapping("/getMeshLockList")
    public CommonResultVo<List<UserDevice>> getMeshLockList(String homeId) {
        ValidUtils.isNullThrow(homeId, "homeId 必传");
        return CommonResultVo.SUCCESS(userDeviceService.list(new QueryWrapper<>(UserDevice.builder()
                .homeId(Long.valueOf(homeId))
                .productType("mesh_lock").build())));
    }
}

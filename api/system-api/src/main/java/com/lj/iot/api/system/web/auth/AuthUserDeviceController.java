package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.DelDeviceBindDto;
import com.lj.iot.biz.base.dto.DeviceIdDto;
import com.lj.iot.biz.base.dto.UserDevicePageDto;
import com.lj.iot.biz.db.smart.entity.DeviceGroup;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.db.smart.entity.UserDevice;
import com.lj.iot.biz.db.smart.service.IDeviceGroupService;
import com.lj.iot.biz.db.smart.service.IUserAccountService;
import com.lj.iot.biz.db.smart.service.IUserDeviceService;
import com.lj.iot.biz.service.BizUserAccountService;
import com.lj.iot.biz.service.BizUserDeviceService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户设备管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/user_device")
public class AuthUserDeviceController {

    @Autowired
    private BizUserDeviceService bizUserDeviceService;

    @Autowired
    private IUserDeviceService userDeviceService;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private BizUserAccountService bizUserAccountService;

    @Autowired
    private IDeviceGroupService deviceGroupService;

    /**
     * 设备表[根据不同的传参，可以查用户设备、房屋设备、房间设备、主控设备、主控下设备、单设备等数据]
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("user:device:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<UserDevice>> devicePage(UserDevicePageDto pageDto) {
        return CommonResultVo.SUCCESS(bizUserDeviceService.customPage(pageDto),
                bizUserDeviceService.statistics(pageDto));
    }

    /**
     * 删除设备
     *
     * @param dto
     * @return
     */
    @PostMapping("delete")
    public CommonResultVo<String> delete(@RequestBody @Valid DeviceIdDto dto) {

        UserDevice userDevice = userDeviceService.getOne(new QueryWrapper<>(UserDevice.builder()
                .deviceId(dto.getDeviceId())
                .build()));
        ValidUtils.isNullThrow(userDevice, "设备不存在");

        //bizUserDeviceService.delete(userDevice);

        //主控设备或者主控对应的虚设备 去掉对应的账号，去掉登录token
        if (userDevice.getMasterDeviceId().equals(userDevice.getPhysicalDeviceId())) {
            UserAccount user = userAccountService.getOne(new QueryWrapper<>(UserAccount.builder()
                    .mobile(userDevice.getMasterDeviceId())
                    .build()));
            if (user != null) {
                bizUserAccountService.cancellation(user.getId());
                LoginUtils.logout(UserDto.builder()
                        .platform(PlatFormEnum.APP.getCode())
                        .uId(user.getId())
                        .account(user.getMobile())
                        .actualUserId(user.getActualUserId())
                        .build());
            }
        }

        //判断删除组后长度是否等于1，如果是1，整个组都要接触
        List<DeviceGroup> list1 = deviceGroupService.list(new QueryWrapper<>(DeviceGroup.builder()
                .groupId(userDevice.getGroupId())
                .build()));
        if (list1.size()<=2&list1.size()>0){
            DelDeviceBindDto delDeviceBindDto = new DelDeviceBindDto();
            delDeviceBindDto.setGroupId(userDevice.getGroupId());
            //删除缓存
            userDeviceService.deleteCacheById(userDevice.getDeviceId());

            userDeviceService.remove(new QueryWrapper<>(UserDevice.builder()
                    .deviceId(userDevice.getDeviceId())
                    .build()));

            bizUserDeviceService.sysDelBindDevice(delDeviceBindDto, UserDto.getUser().getUId());
        }else{
            bizUserDeviceService.delete(userDevice);
        }

        return CommonResultVo.SUCCESS();
    }
}

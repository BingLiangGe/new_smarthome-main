package com.lj.iot.api.hotel.web.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.api.hotel.aop.HotelLogAop;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.HotelUserPageVo;
import com.lj.iot.biz.db.smart.entity.HotelUser;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.biz.db.smart.service.IHotelUserAccountService;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.enums.PlatFormEnum;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.sso.util.LoginUtils;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户
 */
@RestController
@RequestMapping("api/auth/hotel_user")
public class HotelUserController {

    @Autowired
    private IHotelUserAccountService hotelUserAccountService;

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Autowired
    private IHotelUserService hotelUserService;

    /**
     * 设置默认酒店
     */
    @CustomPermissions("hotel_user:set_default")
    @PostMapping("set_default")
    public CommonResultVo<String> setDefault(@RequestBody @Valid HotelIdDto dto) {

        UserDto userDto = UserDto.getUser();
        HotelUser hotelUser = hotelUserService.getOne(new QueryWrapper<>(HotelUser.builder()
                .hotelId(dto.getHotelId())
                .memberUserId(userDto.getUId())
                .build()));
        ValidUtils.isNullThrow(hotelUser, "数据不存在");

        //用户下其他酒店设置为非默
        hotelUserService.update(HotelUser.builder()
                        .isDefault(false)
                        .build(),
                new QueryWrapper<>(HotelUser.builder()
                        .memberUserId(userDto.getUId())
                        .build()));

        //将指定的家设置为默认家
        hotelUserService.updateById(HotelUser.builder()
                .id(hotelUser.getId())
                .isDefault(true).build());

        //获取权限
        List<String> perms = hotelUserService.permissions(hotelUser.getIsMain(), hotelUser.getHotelId(), hotelUser.getMemberUserId());

        //刷新token
        LoginUtils.fresh(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(userDto.getUId())
                .account(userDto.getAccount())
                .perms(perms)
                .actualUserId(userDto.getActualUserId())
                .isMain(hotelUser.getIsMain())
                .hotelId(hotelUser.getHotelId())
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 酒店用户列表
     */
    @GetMapping("/page")
    @CustomPermissions("hotel_user:page")
    public CommonResultVo<IPage<HotelUserPageVo>> list(PageDto dto) {
        return CommonResultVo.SUCCESS(hotelUserService.customPage(dto, UserDto.getUser().getHotelId()));
    }

    /**
     * 用户信息
     */
    @GetMapping("/info")
    @CustomPermissions("hotel_user:info")
    public CommonResultVo<HotelUserAccount> info(@Valid UserIdDto dto) {
        HotelUserAccount user = hotelUserAccountService.getOne(new QueryWrapper<>(HotelUserAccount
                .builder()
                .id(dto.getUserId())
                .actualUserId(UserDto.getUser().getActualUserId())
                .build()));
        ValidUtils.isNullThrow(user, "数据不存在");
        //获取用户所属的角色列表
        List<Long> roleIdList = hotelUserRoleService.queryRoleIdList(UserDto.getUser().getUId(), UserDto.getUser().getHotelId());
        user.setRoleIdList(roleIdList);
        return CommonResultVo.SUCCESS(user);
    }

    /**
     * 新增用户
     */
    @HotelLogAop("新增子用户")
    @PostMapping("/add")
    @CustomPermissions("hotel_user:add")
    public CommonResultVo<HotelUserAccount> add(@RequestBody @Valid HotelUserAccountAddDto dto) {
        UserDto userDto = UserDto.getUser();
        HotelUserAccount userAccount = hotelUserAccountService.add(
                userDto.getIsMain(),
                userDto.getHotelId(),
                userDto.getUId(),
                userDto.getActualUserId(),
                dto);
        return CommonResultVo.SUCCESS(userAccount);
    }

    /**
     * 编辑用户
     */
    @HotelLogAop("编辑用户")
    @PostMapping("/edit")
    @CustomPermissions("hotel_user:edit")
    public CommonResultVo<HotelUserAccount> edit(@RequestBody @Valid HotelUserAccountEditDto dto) {
        UserDto userDto = UserDto.getUser();

        HotelUserAccount userAccount = hotelUserAccountService.edit(
                userDto.getIsMain(),
                userDto.getHotelId(),
                userDto.getUId(),
                userDto.getActualUserId(),
                dto);
        return CommonResultVo.SUCCESS(userAccount);
    }

    /**
     * 删除子账号
     */
    @HotelLogAop("删除子账号")
    @PostMapping("/delete")
    @CustomPermissions("hotel_user:delete")
    public CommonResultVo<String> delete(@RequestBody @Valid UserIdDto dto) {
        UserDto userDto = UserDto.getUser();
        HotelUserAccount user = hotelUserAccountService.delete(userDto.getHotelId(),
                userDto.getActualUserId(),
                dto);

        //退出子账号
        LoginUtils.logout(UserDto.builder()
                .platform(PlatFormEnum.HOTEL.getCode())
                .uId(user.getId())
                .account(user.getMobile())
                .hotelId(userDto.getHotelId())
                .actualUserId(userDto.getActualUserId())
                .isMain(false)
                .build());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("logout")
    public CommonResultVo<String> logout() {
        LoginUtils.logout(UserDto.getUser());
        return CommonResultVo.SUCCESS();
    }
}

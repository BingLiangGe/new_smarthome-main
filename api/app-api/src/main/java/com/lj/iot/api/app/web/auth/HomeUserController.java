package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeIdUserIdDto;
import com.lj.iot.biz.base.dto.HomeUserIdDto;
import com.lj.iot.biz.base.dto.SetHomeUserDeviceAuthorityDto;
import com.lj.iot.biz.base.vo.HomeDataVo;
import com.lj.iot.biz.base.vo.HomeInfoVo;
import com.lj.iot.biz.base.vo.HomeUserVo;
import com.lj.iot.biz.service.BizHomeUserService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 家庭管理
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/home_user")
public class HomeUserController {

    @Resource
    BizHomeUserService bizHomeUserService;

    /**
     * @param dto
     * 设置默认家
     */
    @PostMapping("set_default")
    public CommonResultVo<String> setDefault(@RequestBody @Valid HomeUserIdDto dto) {
        bizHomeUserService.setDefaultHome(dto, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 踢出家庭【管理员用】
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeUserId", type = HomeAuth.PermType.EDIT)
    @PostMapping("kick_out")
    public CommonResultVo<String> kickOut(@RequestBody @Valid HomeUserIdDto dto) {
        bizHomeUserService.kickOut(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * @param dto
     * 退出家庭【成员用】
     */
    @PostMapping("sign_out")
    public CommonResultVo<String> signOut(@RequestBody @Valid HomeUserIdDto dto) {
        bizHomeUserService.signOut(dto, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询用户家庭列表【第一个为默认家庭】
     *
     * @return
     */
    @RequestMapping("list_home")
    public CommonResultVo<List<HomeDataVo>> listHome() {
        return CommonResultVo.SUCCESS(bizHomeUserService.listHome(UserDto.getUser().getUId()));
    }

    /**
     * 家庭信息
     *@param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.ALL)
    @RequestMapping("info")
    public CommonResultVo<HomeInfoVo> info(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.info(dto.getHomeId(), UserDto.getUser().getUId()));
    }

    /**
     * 查询家成员列表
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.MAIN)
    @RequestMapping("list_member")
    public CommonResultVo<List<HomeUserVo>> listMember(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.findHomeUserList(dto.getHomeId(), UserDto.getUser().getUId()));
    }

    /**
     * 设置成员设备权限
     *
     * @param dto
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.MAIN)
    @RequestMapping("setHomeUserDeviceAuthority")
    public CommonResultVo<String>  setHomeUserDeviceAuthority(@Valid SetHomeUserDeviceAuthorityDto dto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.setHomeUserDeviceAuthority(dto, UserDto.getUser().getUId()));
    }



    /**
     * 设置管理员
     *
     * @param dto
     * @return
     */
    @PostMapping("setManage")
    public CommonResultVo<String>  setManage(@RequestBody @Valid HomeUserIdDto dto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.setManage(dto,UserDto.getUser().getUId()));
    }

}

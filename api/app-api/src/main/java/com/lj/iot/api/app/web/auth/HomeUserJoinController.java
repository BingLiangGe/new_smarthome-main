package com.lj.iot.api.app.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeUserJoinHandleDto;
import com.lj.iot.biz.base.dto.InviteUserDto;
import com.lj.iot.biz.base.vo.HomeUserJoinVo;
import com.lj.iot.biz.service.BizHomeUserJoinService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 家庭成员申请邀请接口
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/home_user_join")
public class HomeUserJoinController {
    @Autowired
    private BizHomeUserJoinService bizHomeUserJoinService;

    /**
     * 主账号数据
     */
    @HomeAuth(type = HomeAuth.PermType.EDIT)
    @RequestMapping("page_my_home")
    public CommonResultVo<IPage<HomeUserJoinVo>> pageMyHome(@Valid PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizHomeUserJoinService.pageMyHome(pageDto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 成员数据
     */
    @HomeAuth(type = HomeAuth.PermType.EDIT)
    @RequestMapping("page_other_home")
    public CommonResultVo<IPage<HomeUserJoinVo>> pageOtherHome(@Valid PageDto pageDto) {
        return CommonResultVo.SUCCESS(bizHomeUserJoinService.pageOtherHome(pageDto, UserDto.getUser().getActualUserId()));
    }

    /**
     * 邀请
     */
    //@HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("invite")
    public CommonResultVo<String> invite(@RequestBody @Valid InviteUserDto dto) {
        bizHomeUserJoinService.invite(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 处理邀请
     */
    //@HomeAuth(value = "homeId", type = HomeAuth.PermType.EDIT)
    @PostMapping("handle_invite")
    public CommonResultVo<Long> handleInvite(@RequestBody @Valid HomeUserJoinHandleDto dto) {
        bizHomeUserJoinService.handleInvite(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 申请
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("apply")
    public CommonResultVo<String> apply(@RequestBody @Valid HomeIdDto dto) {
        bizHomeUserJoinService.apply(dto, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }


    /**
     * 处理申请
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("handle_apply")
    public CommonResultVo<String> handleApply(@RequestBody @Valid HomeUserJoinHandleDto dto) {
        bizHomeUserJoinService.handleApply(dto, UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }
}

package com.lj.iot.api.hotel.web.auth;

import com.lj.iot.api.hotel.aop.CustomPermissions;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.base.dto.HomeUserIdDto;
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
     * 踢出家庭【管理员用】
     *
     * @param dto
     * @return
     */
    @PostMapping("kick_out")
    @CustomPermissions("home_user:kick_out")
    public CommonResultVo<String> kickOut(@RequestBody @Valid HomeUserIdDto dto) {
        bizHomeUserService.kickOut(dto, UserDto.getUser().getActualUserId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 查询家成员列表
     *
     * @param dto
     * @return
     */
    @RequestMapping("list_member")
    @CustomPermissions("home_user:list_member")
    public CommonResultVo<List<HomeUserVo>> listMember(@Valid HomeIdDto dto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.findHomeUserList(dto.getHomeId(), UserDto.getUser().getActualUserId()));
    }
}

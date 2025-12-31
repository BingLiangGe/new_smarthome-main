package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import com.lj.iot.biz.service.BizHomeRoomService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 房间管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/home_room")
public class AuthHomeRoomController {

    @Resource
    BizHomeRoomService bizHomeRoomService;

    /**
     * 家庭-房间列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("home:room:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<HomeRoom>> homeRoomPage(HomeRoomPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizHomeRoomService.customPage(pageDto));
    }

}

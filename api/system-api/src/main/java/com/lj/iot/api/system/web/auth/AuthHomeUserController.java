package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.db.smart.entity.HomeUser;
import com.lj.iot.biz.service.BizHomeUserService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成员管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/home_user")
public class AuthHomeUserController {

    @Autowired
    private BizHomeUserService bizHomeUserService;

    /**
     * 家庭-成员表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("home:user:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<HomeUser>> homeUserPage(HomeUserPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizHomeUserService.customPage(pageDto));
    }

}

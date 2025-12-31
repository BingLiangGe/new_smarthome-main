package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.biz.db.smart.entity.UserAccount;
import com.lj.iot.biz.service.BizUserService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/user")
public class AuthUserController {

    @Autowired
    private BizUserService bizUserService;

    /**
     * 用户列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("user:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<UserAccount>> page(PageDto pageDto) {

        return CommonResultVo.SUCCESS(bizUserService.customPage(pageDto));
    }
}

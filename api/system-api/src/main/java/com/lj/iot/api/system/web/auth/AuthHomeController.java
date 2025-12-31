package com.lj.iot.api.system.web.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.*;
import com.lj.iot.biz.base.vo.HomePageVo;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.service.*;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 家庭管理
 *
 * @author mz
 * @Date 2022/7/18
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth/home")
public class AuthHomeController {

    @Autowired
    private BizHomeService bizHomeService;

    @Autowired
    private BizSosContactService bizSosContactService;


    /**
     * 家庭列表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("home:page")
    @RequestMapping("/page")
    public CommonResultVo<IPage<HomePageVo>> page(HomeRoomPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizHomeService.customPage(pageDto));
    }

    /**
     * 家庭-通讯录表
     *
     * @param pageDto
     * @return
     */
    @CustomPermissions("home:sos:page")
    @RequestMapping("/sos_page")
    public CommonResultVo<IPage<SosContact>> homeSosPage(HomeUserPageDto pageDto) {
        return CommonResultVo.SUCCESS(bizSosContactService.customPage(pageDto));
    }
}

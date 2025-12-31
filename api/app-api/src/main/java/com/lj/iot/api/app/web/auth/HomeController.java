package com.lj.iot.api.app.web.auth;

import com.lj.iot.api.app.aop.HomeAuth;
import com.lj.iot.biz.base.dto.HomeAddDto;
import com.lj.iot.biz.base.dto.HomeEditDto;
import com.lj.iot.biz.base.dto.HomeIdDto;
import com.lj.iot.biz.db.smart.entity.Home;
import com.lj.iot.biz.service.BizHomeService;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * 用户家相关接口
 *
 * @author wanyuli
 * @since 2022-07-19
 */
@RestController
@RequestMapping("api/auth/home")
public class HomeController {
    @Resource
    BizHomeService bizHomeService;

    /**
     * 添加家庭
     */
    @HomeAuth(type = HomeAuth.PermType.MAIN)
    @PostMapping("add")
    public CommonResultVo<Home> add(@RequestBody @Valid HomeAddDto dto) {
        return CommonResultVo.SUCCESS(bizHomeService.add(dto, UserDto.getUser().getUId()));
    }

    /**
     * 编辑家庭
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.MAIN)
    @PostMapping("edit")
    public CommonResultVo<Home> edit(@RequestBody @Valid HomeEditDto dto) {
        return CommonResultVo.SUCCESS(bizHomeService.edit(dto, UserDto.getUser().getUId()));
    }

    /**
     * 删除家
     *
     * @param dto 家ID
     * @return
     */
    @HomeAuth(value = "homeId", type = HomeAuth.PermType.MAIN)
    @PostMapping("delete")
    public CommonResultVo deleteHomeById(@RequestBody @Valid HomeIdDto dto) {
        bizHomeService.deleteHomeById(dto.getHomeId(), UserDto.getUser().getUId());
        return CommonResultVo.SUCCESS();
    }
}

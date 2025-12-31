/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.IdsDto;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.system.aop.SysLogAop;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.dto.SysRoleAddDto;
import com.lj.iot.common.system.dto.SysRoleEditDto;
import com.lj.iot.common.system.entity.SysRole;
import com.lj.iot.common.system.service.ISysRoleMenuService;
import com.lj.iot.common.system.service.ISysRoleService;
import com.lj.iot.common.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("api/auth/sys/role")
public class SysRoleController {
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    /**
     * 角色列表
     */
    @GetMapping("/list")
    @CustomPermissions("sys:role:list")
    public CommonResultVo list(PageDto pageDto) {
        //如果不是超级管理员，则只查询自己创建的角色列表
        UserDto userDto = UserDto.getUser();
        IPage<SysRole> page = sysRoleService.page(PageUtil.page(pageDto), new QueryWrapper<SysRole>()
                .like(StringUtils.isNotBlank(pageDto.getSearch()), "role_name", pageDto.getSearch())
                .eq(userDto.getLongId() != Constant.SUPER_ADMIN, "create_user_id", userDto.getUId()));

        return CommonResultVo.SUCCESS(page);
    }

    /**
     * 角色列表
     */
    @GetMapping("/select")
    @CustomPermissions("sys:role:select")
    public CommonResultVo select() {
        UserDto userDto = UserDto.getUser();

        List<SysRole> list = sysRoleService.list(new QueryWrapper<SysRole>()
                .eq(userDto.getLongId() != Constant.SUPER_ADMIN, "create_user_id", userDto.getUId()));

        return CommonResultVo.SUCCESS(list);
    }

    /**
     * 角色信息
     */
    @GetMapping("/info/{roleId}")
    @CustomPermissions("sys:role:info")
    public CommonResultVo info(@PathVariable("roleId") Long roleId) {
        SysRole role = sysRoleService.getById(roleId);

        //查询角色对应的菜单
        List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
        role.setMenuIdList(menuIdList);

        return CommonResultVo.SUCCESS(role);

    }

    /**
     * 保存角色
     */
    @SysLogAop("保存角色")
    @PostMapping("/save")
    @CustomPermissions("sys:role:save")
    public CommonResultVo save(SysRoleAddDto dto) {
        UserDto userDto = UserDto.getUser();

        sysRoleService.saveRole(dto, userDto.getLongId());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 修改角色
     */
    @SysLogAop("修改角色")
    @PostMapping("/update")
    @CustomPermissions("sys:role:update")
    public CommonResultVo update(@Valid SysRoleEditDto dto) {

        sysRoleService.update(dto, UserDto.getUser().getLongId());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除角色
     */
    @SysLogAop("删除角色")
    @PostMapping("/delete")
    @CustomPermissions("sys:role:delete")
    public CommonResultVo delete(IdsDto dto) {
        Long[] roleIds=dto.getIds();
        sysRoleService.deleteBatch(roleIds);

        return CommonResultVo.SUCCESS();
    }
}

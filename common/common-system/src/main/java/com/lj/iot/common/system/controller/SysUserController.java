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
import com.lj.iot.common.system.dto.SysPasswordDto;
import com.lj.iot.common.system.dto.SysUserAddDto;
import com.lj.iot.common.system.dto.SysUserEditDto;
import com.lj.iot.common.system.entity.SysUser;
import com.lj.iot.common.system.service.ISysUserRoleService;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.util.MD5Utils;
import com.lj.iot.common.util.PageUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("api/auth/sys/user")
public class SysUserController {
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;


    /**
     * 所有用户列表
     */
    @GetMapping("/list")
    @CustomPermissions("sys:user:list")
    public CommonResultVo list(PageDto pageDto) {
        //只有超级管理员，才能查看所有管理员列表
        UserDto userDto = UserDto.getUser();
        IPage<SysUser> page = sysUserService.page(PageUtil.page(pageDto),
                new QueryWrapper<SysUser>()
                        .like(StringUtils.isNotBlank(pageDto.getSearch()), "username", pageDto.getSearch())
                        .eq(userDto.getLongId() != Constant.SUPER_ADMIN, "create_user_id", userDto.getUId())
        );

        for (SysUser record : page.getRecords()) {
            List<Long> roleIdList = sysUserRoleService.queryRoleIdList(record.getUserId());
            record.setRoleIdList(roleIdList);
        }

        return CommonResultVo.SUCCESS(page);
    }

    /**
     * 获取登录的用户信息
     */
    @GetMapping("/info")
    public CommonResultVo<SysUser> info() {
        SysUser sysUser = sysUserService.getById(UserDto.getUser().getUId());
        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(sysUser.getUserId());
        sysUser.setRoleIdList(roleIdList);
        return CommonResultVo.SUCCESS(sysUser);
    }

    /**
     * 修改登录用户密码
     */
    @SysLogAop("修改密码")
    @PostMapping("/password")
    public CommonResultVo password(@Valid SysPasswordDto form) {

        SysUser sysUser = sysUserService.getById(UserDto.getUser().getUId());

        String password = MD5Utils.md5(form.getPassword(), sysUser.getSalt());
        String newPassword = MD5Utils.md5(form.getNewPassword(), sysUser.getSalt());

        //更新密码
        boolean flag = sysUserService.updatePassword(sysUser.getUserId(), password, newPassword);
        if (!flag) {
            return CommonResultVo.FAILURE_MSG("原密码不正确");
        }

        return CommonResultVo.SUCCESS();
    }

    /**
     * 用户信息
     */
    @GetMapping("/info/{userId}")
    @CustomPermissions("sys:user:info")
    public CommonResultVo info(@PathVariable("userId") Long userId) {
        SysUser user = sysUserService.getById(userId);
        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);
        return CommonResultVo.SUCCESS(user);
    }

    /**
     * 保存用户
     */
    @SysLogAop("保存用户")
    @PostMapping("/save")
    @CustomPermissions("sys:user:save")
    public CommonResultVo save(@Valid SysUserAddDto dto) {
        sysUserService.saveUser(dto, UserDto.getUser().getLongId());
        return CommonResultVo.SUCCESS();
    }

    /**
     * 修改用户
     */
    @SysLogAop("修改用户")
    @PostMapping("/update")
    @CustomPermissions("sys:user:update")
    public CommonResultVo update(@Valid SysUserEditDto dto) {

        sysUserService.update(dto, UserDto.getUser().getLongId());

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除用户
     */
    @SysLogAop("删除用户")
    @PostMapping("/delete")
    @CustomPermissions("sys:user:delete")
    public CommonResultVo delete(IdsDto dto) {
        Long[] userIds = dto.getIds();
        if (ArrayUtils.contains(userIds, 1L)) {
            return CommonResultVo.FAILURE_MSG("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, UserDto.getUser().getUId())) {
            return CommonResultVo.FAILURE_MSG("当前用户不能删除");

        }

        sysUserService.deleteBatch(userIds);

        return CommonResultVo.SUCCESS();
    }
}

/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.common.system.controller;

import com.lj.iot.common.base.dto.UserDto;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.system.aop.CustomPermissions;
import com.lj.iot.common.system.aop.SysLogAop;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.entity.SysMenu;
import com.lj.iot.common.system.service.ISysMenuService;
import com.lj.iot.common.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 系统菜单
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("api/auth/sys/menu")
public class SysMenuController {
    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 导航菜单
     */
    @GetMapping("/nav")
    public CommonResultVo nav() {
        UserDto userDto = UserDto.getUser();
        List<SysMenu> menuList = sysMenuService.getUserMenuList(userDto.getLongId());
        Set<String> permissions = sysUserService.getUserPermissions(userDto.getLongId());
        Map<String, Object> map = new HashMap();
        map.put("menuList", menuList);
        map.put("permissions", permissions);
        return CommonResultVo.SUCCESS(map);
    }

    /**
     * 所有菜单列表
     */
    @GetMapping("/list")
    @CustomPermissions("sys:menu:list")
    public CommonResultVo<List<SysMenu>> list() {
        List<SysMenu> menuList = sysMenuService.list();
        HashMap<Long, SysMenu> menuMap = new HashMap<>(12);
        for (SysMenu s : menuList) {
            menuMap.put(s.getMenuId(), s);
        }
        for (SysMenu s : menuList) {
            SysMenu parent = menuMap.get(s.getParentId());
            if (Objects.nonNull(parent)) {
                s.setParentName(parent.getName());
            }

        }

        return CommonResultVo.SUCCESS(menuList);

    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @GetMapping("/select")
    @CustomPermissions("sys:menu:select")
    public CommonResultVo select() {
        //查询列表数据
        List<SysMenu> menuList = sysMenuService.queryNotButtonList();

        //添加顶级菜单
        SysMenu root = new SysMenu();
        root.setMenuId(0L);
        root.setName("一级菜单");
        root.setParentId(-1L);
        root.setOpen(true);
        menuList.add(root);

        return CommonResultVo.SUCCESS(menuList);
    }

    /**
     * 菜单信息
     */
    @GetMapping("/info/{menuId}")
    @CustomPermissions("sys:menu:info")
    public CommonResultVo info(@PathVariable("menuId") Long menuId) {
        SysMenu menu = sysMenuService.getById(menuId);
        return CommonResultVo.SUCCESS(menu);
    }

    /**
     * 保存
     */
    @SysLogAop("保存菜单")
    @PostMapping("/save")
    @CustomPermissions("sys:menu:save")
    public CommonResultVo save(SysMenu menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.save(menu);

        return CommonResultVo.SUCCESS();
    }

    /**
     * 修改
     */
    @SysLogAop("修改菜单")
    @PostMapping("/update")
    @CustomPermissions("sys:menu:update")
    public CommonResultVo<String> update(SysMenu menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.updateById(menu);

        return CommonResultVo.SUCCESS();
    }

    /**
     * 删除
     */
    @SysLogAop("删除菜单")
    @PostMapping("/delete/{menuId}")
    @CustomPermissions("sys:menu:delete")
    public CommonResultVo delete(@PathVariable("menuId") long menuId) {
        /*if (menuId < 31) {
            return CommonResultVo.FAILURE_MSG("系统菜单，不能删除");

        }*/

        //判断是否有子菜单或按钮
        List<SysMenu> menuList = sysMenuService.queryListParentId(menuId);
        if (menuList.size() > 0) {
            return CommonResultVo.FAILURE_MSG("请先删除子菜单或按钮");
        }

        sysMenuService.delete(menuId);

        return CommonResultVo.SUCCESS();
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenu menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw CommonException.FAILURE("菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw CommonException.FAILURE("上级菜单不能为空");
        }

        //菜单
        if (menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw CommonException.FAILURE("菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = Constant.MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenu parentMenu = sysMenuService.getById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == Constant.MenuType.CATALOG.getValue() ||
                menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (parentType != Constant.MenuType.CATALOG.getValue()) {
                throw CommonException.FAILURE("上级菜单只能为目录类型");
            }
            return;
        }

        //按钮
        if (menu.getType() == Constant.MenuType.BUTTON.getValue()) {
            if (parentType != Constant.MenuType.MENU.getValue()) {
                throw CommonException.FAILURE("上级菜单只能为菜单类型");
            }
        }
    }
}

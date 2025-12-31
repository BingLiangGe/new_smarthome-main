/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.lj.iot.common.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.entity.SysMenu;
import com.lj.iot.common.system.entity.SysUser;
import com.lj.iot.common.system.service.ISysMenuService;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.system.service.ShiroService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@DS("system")
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            List<SysMenu> menuList = sysMenuService.list();
            permsList = new ArrayList<>(menuList.size());
            for (SysMenu menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = sysUserService.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUser queryUser(Long userId) {
        return sysUserService.getById(userId);
    }
}

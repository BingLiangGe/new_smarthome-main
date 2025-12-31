package com.lj.iot.common.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.entity.SysMenu;
import com.lj.iot.common.system.mapper.SysMenuMapper;
import com.lj.iot.common.system.service.ISysMenuService;
import com.lj.iot.common.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
@DS("system")
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public List<SysMenu> queryListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenu> menuList = queryListParentId(parentId);
        if (menuIdList == null) {
            return menuList;
        }

        List<SysMenu> userMenuList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menuIdList.contains(menu.getMenuId())) {
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    @Override
    public List<SysMenu> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public List<SysMenu> queryNotButtonList() {
        return baseMapper.queryNotButtonList();
    }

    @Override
    public List<SysMenu> getUserMenuList(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            return getMenuList(null);
        }

        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        return getMenuList(menuIdList);
    }

    @Override
    public void delete(Long menuId) {
        this.removeById(menuId);
    }

    /**
     * 获取拥有的菜单列表
     *
     * @param menuIdList
     * @return
     */
    private List<SysMenu> getMenuList(List<Long> menuIdList) {
        // 查询拥有的所有菜单
        List<SysMenu> menus = this.baseMapper.selectList(new QueryWrapper<SysMenu>()
                .in(Objects.nonNull(menuIdList) && menuIdList.size() > 0, "menu_id", menuIdList)
                .in("type", 0, 1));
        // 将id和菜单绑定
        HashMap<Long, SysMenu> menuMap = new HashMap<>(12);
        for (SysMenu s : menus) {
            menuMap.put(s.getMenuId(), s);
        }
        // 使用迭代器,组装菜单的层级关系
        Iterator<SysMenu> iterator = menus.iterator();
        while (iterator.hasNext()) {
            SysMenu menu = iterator.next();
            SysMenu parent = menuMap.get(menu.getParentId());
            if (Objects.nonNull(parent)) {
                parent.getList().add(menu);
                // 将这个菜单从当前节点移除
                iterator.remove();
            }
        }

        return menus;
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenu> getAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenu> menuList = queryListParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenu> getMenuTreeList(List<SysMenu> menuList, List<Long> menuIdList) {
        List<SysMenu> subMenuList = new ArrayList<SysMenu>();

        for (SysMenu entity : menuList) {
            //目录
            if (entity.getType() == Constant.MenuType.CATALOG.getValue()) {
                entity.setList(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }
}

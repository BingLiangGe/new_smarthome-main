package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.HotelMenu;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;
import com.lj.iot.biz.db.smart.mapper.HotelMenuMapper;
import com.lj.iot.biz.db.smart.service.IHotelMenuService;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 酒店菜单管理 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelMenuServiceImpl extends ServiceImpl<HotelMenuMapper, HotelMenu> implements IHotelMenuService {

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Override
    public List<HotelMenu> nav(Boolean isMain, Long hotelId, String hotelUserId) {

        List<HotelMenu> hotelMenuList;
        if (isMain) {
            hotelMenuList = this.mainNav();
        } else {
            HotelUserRole hotelUserRole = hotelUserRoleService.getOne(new QueryWrapper<>(HotelUserRole.builder()
                    .hotelId(hotelId)
                    .hotelUserId(hotelUserId)
                    .build()));
            ValidUtils.isNullThrow(hotelUserRole, "没有权限");
            hotelMenuList = this.roleNav(hotelUserRole.getRoleId());
        }
        return list2Tree(hotelMenuList);
    }


    private List<HotelMenu> list2Tree(List<HotelMenu> list) {
        // 将id和菜单绑定
        HashMap<Long, HotelMenu> menuMap = new HashMap<>();
        for (HotelMenu s : list) {
            menuMap.put(s.getMenuId(), s);
        }
        // 使用迭代器,组装菜单的层级关系
        Iterator<HotelMenu> iterator = list.iterator();
        while (iterator.hasNext()) {
            HotelMenu menu = iterator.next();
            HotelMenu parent = menuMap.get(menu.getParentId());
            if (Objects.nonNull(parent)) {
                menu.setParentName(parent.getName());
                parent.getList().add(menu);
                // 将这个菜单从当前节点移除
                iterator.remove();
            }
        }
        return list;
    }

    @Override
    public List<HotelMenu> mainNav() {
        return this.baseMapper.mainNav();
    }

    @Override
    public List<HotelMenu> roleNav(Long roleId) {
        return this.baseMapper.roleNav(roleId);
    }

    @Override
    public List<HotelMenu> mainMenu() {
        return this.baseMapper.mainMenu();
    }

    @Override
    public List<HotelMenu> allMenu() {
        List<HotelMenu> list = this.mainMenu();
        return list2Tree(list);
    }

    @Override
    public List<HotelMenu> roleMenu(Long roleId) {
        return this.baseMapper.roleMenu(roleId);
    }

    @Override
    public List<Long> queryMenuIdList(Long roleId) {
        return this.baseMapper.queryMenuIdList(roleId);    }
}

package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.HotelRoleMenu;
import com.lj.iot.biz.db.smart.mapper.HotelRoleMenuMapper;
import com.lj.iot.biz.db.smart.service.IHotelRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 酒店角色与菜单对应关系 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelRoleMenuServiceImpl extends ServiceImpl<HotelRoleMenuMapper, HotelRoleMenu> implements IHotelRoleMenuService {

    @Override
    public List<String> permissions(Long hotelId, Long roleId) {
        return this.baseMapper.permissions(hotelId, roleId);
    }

    @Override
    public List<String> mainPermissions() {
        return this.baseMapper.mainPermissions();
    }

    @Override
    public void saveOrUpdate(Long hotelId, Long roleId, List<Long> menuIdList) {
        //先删除角色与菜单关系
        this.remove(new QueryWrapper<>(HotelRoleMenu.builder()
                .roleId(roleId)
                .build()));

        if (menuIdList.size() == 0) {
            return;
        }

        //保存角色与菜单关系
        for (Long menuId : menuIdList) {
            HotelRoleMenu roleMenu = HotelRoleMenu.builder()
                    .hotelId(hotelId)
                    .roleId(roleId)
                    .menuId(menuId)
                    .build();
            this.save(roleMenu);
        }
    }
}

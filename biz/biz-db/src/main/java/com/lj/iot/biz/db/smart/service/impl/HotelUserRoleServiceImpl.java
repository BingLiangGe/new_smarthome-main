package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;
import com.lj.iot.biz.db.smart.mapper.HotelUserRoleMapper;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 酒店用户与角色对应关系 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelUserRoleServiceImpl extends ServiceImpl<HotelUserRoleMapper, HotelUserRole> implements IHotelUserRoleService {

    @Override
    public void saveOrUpdate(Long hotelId, String hotelUserId, Long roleId) {
        //先删除用户与角色关系
        this.remove(new QueryWrapper<>(HotelUserRole.builder()
                .hotelId(hotelId)
                .hotelUserId(hotelUserId)
                .build()));

        //保存用户与角色关系
        HotelUserRole hotelUserRole = HotelUserRole.builder()
                .hotelId(hotelId)
                .hotelUserId(hotelUserId)
                .roleId(roleId)
                .build();
        this.save(hotelUserRole);
    }

    @Override
    public List<Long> queryRoleIdList(String hotelUserId, Long hotelId) {
        return baseMapper.queryRoleIdList(hotelUserId, hotelId);
    }

    @Override
    public void delete(Long hotelId, String hotelUserId) {
        //先删除用户与角色关系
        this.remove(new QueryWrapper<>(HotelUserRole.builder()
                .hotelId(hotelId)
                .hotelUserId(hotelUserId)
                .build()));
    }
}

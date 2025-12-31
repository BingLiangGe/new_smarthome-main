package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.dto.HotelRoleAddDto;
import com.lj.iot.biz.base.dto.HotelRoleEditDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.HotelRole;
import com.lj.iot.biz.db.smart.entity.HotelRoleMenu;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;
import com.lj.iot.biz.db.smart.mapper.HotelRoleMapper;
import com.lj.iot.biz.db.smart.service.IHotelMenuService;
import com.lj.iot.biz.db.smart.service.IHotelRoleMenuService;
import com.lj.iot.biz.db.smart.service.IHotelRoleService;
import com.lj.iot.biz.db.smart.service.IHotelUserRoleService;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.PageUtil;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 酒店角色 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelRoleServiceImpl extends ServiceImpl<HotelRoleMapper, HotelRole> implements IHotelRoleService {

    @Autowired
    private IHotelMenuService hotelMenuService;

    @Autowired
    private IHotelUserRoleService hotelUserRoleService;

    @Autowired
    private IHotelRoleMenuService hotelRoleMenuService;

    public CommonResultVo<HotelRole> addParent(HotelRoleAddDto dto, Boolean isMain, Long hotelId, String hotelUserId) {
        //权限越界校验
        checkPerms(dto.getMenuIdList(), isMain, hotelId, hotelUserId);

        HotelRole role = HotelRole.builder()
                .hotelId(hotelId)
                .roleName(dto.getRoleName())
                .remark(dto.getRemark())
                .menuIdList(dto.getMenuIdList())
                .createUserId(hotelUserId)
                .build();
        HotelRole one = this.getOne(new QueryWrapper<>(HotelRole.builder().roleName(dto.getRoleName()).hotelId(hotelId).build()));

        if (one != null){
            return CommonResultVo.INSTANCE(-5,"角色已存在",one);
        }
        this.save(role);

        //保存角色与菜单关系
        hotelRoleMenuService.saveOrUpdate(hotelId, role.getRoleId(), role.getMenuIdList());

        return CommonResultVo.SUCCESS(role);
    }

    @DSTransactional
    @Override
    public HotelRole add(HotelRoleAddDto dto, Boolean isMain, Long hotelId, String hotelUserId) {

        //权限越界校验
        checkPerms(dto.getMenuIdList(), isMain, hotelId, hotelUserId);

        HotelRole role = HotelRole.builder()
                .hotelId(hotelId)
                .roleName(dto.getRoleName())
                .remark(dto.getRemark())
                .menuIdList(dto.getMenuIdList())
                .createUserId(hotelUserId)
                .build();
        HotelRole one = this.getOne(new QueryWrapper<>(HotelRole.builder().roleName(dto.getRoleName()).hotelId(hotelId).build()));
        ValidUtils.noNullThrow(one, "角色名重复");
        this.save(role);

        //保存角色与菜单关系
        hotelRoleMenuService.saveOrUpdate(hotelId, role.getRoleId(), role.getMenuIdList());

        return role;
    }

    /**
     * 检查权限是否越权
     */
    @Override
    public void checkPerms(List<Long> menuIdList, Boolean isMain, Long hotelId, String hotelUserId) {

        if (isMain) {
            return;
        }
        HotelUserRole hotelUserRole = hotelUserRoleService.getOne(new QueryWrapper<>(HotelUserRole.builder()
                .hotelId(hotelId)
                .hotelUserId(hotelUserId)
                .build()));
        ValidUtils.isNullThrow(hotelUserRole, "用户没有权限");

        List<Long> hasMenuIdList = hotelMenuService.queryMenuIdList(hotelUserRole.getRoleId());

        //判断是否越权
        ValidUtils.isFalseThrow(new HashSet<>(hasMenuIdList).containsAll(menuIdList), "新增角色的权限，已超出你的权限范围");
    }

    @Override
    public void checkPerms(Long roleId, Boolean isMain, Long hotelId, String hotelUserId) {
        HotelRole role = this.getOne(new QueryWrapper<>(HotelRole.builder()
                .hotelId(hotelId)
                .roleId(roleId)
                .build()));
        ValidUtils.isNullThrow(role, "角色不存在");
        List<Long> menuIdList = hotelMenuService.queryMenuIdList(role.getRoleId());
        checkPerms(menuIdList, isMain, hotelId, hotelUserId);
    }

    @DSTransactional
    @Override
    public HotelRole edit(HotelRoleEditDto dto, Boolean isMain, Long hotelId, String hotelUserId) {

        //权限越界校验
        checkPerms(dto.getMenuIdList(), isMain, hotelId, hotelUserId);

        HotelRole db = this.getOne(new QueryWrapper<>(HotelRole.builder()
                .roleId(dto.getRoleId())
                .hotelId(hotelId)
                .build()));
        ValidUtils.isNullThrow(db, "数据不存在");

        HotelRole role = HotelRole.builder()
                .roleId(db.getRoleId())
                .hotelId(hotelId)
                .roleName(dto.getRoleName())
                .remark(dto.getRemark())
                .menuIdList(dto.getMenuIdList())
                .createUserId(hotelUserId)
                .build();

        this.updateById(role);

        //保存角色与菜单关系
        hotelRoleMenuService.saveOrUpdate(hotelId, role.getRoleId(), role.getMenuIdList());
        role = this.getById(role.getRoleId());
        role.setMenuIdList(dto.getMenuIdList());
        return role;
    }

    @DSTransactional
    @Override
    public void delete(IdDto dto, Long hotelId, String hotelUserId) {

        //删除角色
        HotelRole hotelRole = this.getOne(new QueryWrapper<>(HotelRole.builder()
                .roleId(dto.getId())
                .hotelId(hotelId)
                .build()));
        ValidUtils.isNullThrow(hotelRole, "角色不存");

        this.removeById(hotelRole.getRoleId());

        //删除角色与菜单关联
        hotelRoleMenuService.remove(new QueryWrapper<>(HotelRoleMenu.builder()
                .roleId(hotelRole.getRoleId())
                .hotelId(hotelRole.getHotelId())
                .build()));

        //删除角色与用户关联
        hotelUserRoleService.remove(new QueryWrapper<>(HotelUserRole.builder()
                .roleId(hotelRole.getRoleId())
                .hotelId(hotelRole.getHotelId())
                .build()));
    }

    @Override
    public IPage<HotelRole> customPage(PageDto pageDto, Long hotelId) {
        return this.baseMapper.customPage(PageUtil.page(pageDto), pageDto, hotelId);
    }
}

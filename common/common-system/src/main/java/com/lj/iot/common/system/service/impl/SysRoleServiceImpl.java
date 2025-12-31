package com.lj.iot.common.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.dto.SysRoleAddDto;
import com.lj.iot.common.system.dto.SysRoleEditDto;
import com.lj.iot.common.system.entity.SysRole;
import com.lj.iot.common.system.mapper.SysRoleMapper;
import com.lj.iot.common.system.service.ISysRoleMenuService;
import com.lj.iot.common.system.service.ISysRoleService;
import com.lj.iot.common.system.service.ISysUserRoleService;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
@DS("system")
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private ISysRoleMenuService sysRoleMenuService;


    @Autowired
    private ISysUserRoleService sysUserRoleService;


    @Autowired
    private ISysUserService sysUserService;

    @Override
    @DSTransactional
    public void saveRole(SysRoleAddDto dto, Long userId) {
        SysRole sysRole = SysRole.builder()
                .roleName(dto.getRoleName())
                .remark(dto.getRemark())
                .menuIdList(dto.getMenuIdList())
                .createUserId(userId)
                .build();

        //检查权限是否越权
        checkPrems(sysRole);

        this.save(sysRole);

        //保存角色与菜单关系
        sysRoleMenuService.saveOrUpdate(sysRole.getRoleId(), sysRole.getMenuIdList());
    }

    @Override
    @DSTransactional
    public void update(SysRoleEditDto dto, Long userId) {
        SysRole db = getById(dto.getRoleId());
        ValidUtils.isNullThrow(db, "数据不存在");
        SysRole sysRole = SysRole.builder()
                .roleId(db.getRoleId())
                .roleName(dto.getRoleName())
                .menuIdList(dto.getMenuIdList())
                .remark(dto.getRemark())
                .createUserId(userId)
                .build();

        //检查权限是否越权
        checkPrems(sysRole);

        this.updateById(sysRole);

        //更新角色与菜单关系
        sysRoleMenuService.saveOrUpdate(sysRole.getRoleId(), sysRole.getMenuIdList());
    }

    @Override
    @DSTransactional
    public void deleteBatch(Long[] roleIds) {
        //删除角色
        this.removeByIds(Arrays.asList(roleIds));

        //删除角色与菜单关联
        sysRoleMenuService.deleteBatch(roleIds);

        //删除角色与用户关联
        sysUserRoleService.deleteBatch(roleIds);
    }


    @Override
    public List<Long> queryRoleIdList(Long createUserId) {
        return baseMapper.queryRoleIdList(createUserId);
    }

    /**
     * 检查权限是否越权
     */
    private void checkPrems(SysRole role) {
        //如果不是超级管理员，则需要判断角色的权限是否超过自己的权限
        if (role.getCreateUserId() == Constant.SUPER_ADMIN) {
            return;
        }

        //查询用户所拥有的菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(role.getCreateUserId());

        //判断是否越权
        if (!menuIdList.containsAll(role.getMenuIdList())) {
            throw CommonException.FAILURE("新增角色的权限，已超出你的权限范围");
        }
    }
}

package com.lj.iot.common.system.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.base.exception.CommonException;
import com.lj.iot.common.system.constant.Constant;
import com.lj.iot.common.system.dto.SysUserAddDto;
import com.lj.iot.common.system.dto.SysUserEditDto;
import com.lj.iot.common.system.entity.SysMenu;
import com.lj.iot.common.system.entity.SysUser;
import com.lj.iot.common.system.mapper.SysUserMapper;
import com.lj.iot.common.system.service.ISysMenuService;
import com.lj.iot.common.system.service.ISysRoleService;
import com.lj.iot.common.system.service.ISysUserRoleService;
import com.lj.iot.common.system.service.ISysUserService;
import com.lj.iot.common.util.MD5Utils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
@DS("system")
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysMenuService sysMenuService;


    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public List<String> queryAllPerms(Long userId) {
        return baseMapper.queryAllPerms(userId);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }

    @Override
    public SysUser queryByUserName(String username) {
        return this.getOne(new QueryWrapper<>(SysUser.builder()
                .username(username)
                .build()));
    }

    @Override
    @DSTransactional
    public void saveUser(SysUserAddDto dto, Long userId) {
        String salt = RandomStringUtils.randomAlphanumeric(5);

        SysUser sysUser = SysUser.builder()
                .username(dto.getUsername())
                .password(MD5Utils.md5(dto.getPassword(), salt))
                .salt(salt)
                .email(dto.getEmail())
                .mobile(dto.getMobile())
                .createUserId(userId)
                .build();
        this.save(sysUser);

        //检查角色是否越权
        checkRole(sysUser);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(sysUser.getUserId(), sysUser.getRoleIdList());
    }

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
            permsList = this.baseMapper.queryAllPerms(userId);
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
    @DSTransactional
    public void update(SysUserEditDto dto, Long userId) {
        SysUser db = sysUserService.getById(dto.getUserId());
        SysUser sysUser = new SysUser();
        sysUser.setUserId(db.getUserId());
        sysUser.setCreateUserId(db.getCreateUserId());
        BeanUtils.copyProperties(dto, sysUser);
        if (StringUtils.isBlank(sysUser.getPassword())) {
            sysUser.setPassword(null);
        } else {
            String salt = RandomStringUtils.randomAlphanumeric(5);
            sysUser.setPassword(MD5Utils.md5(sysUser.getPassword(), salt));
            sysUser.setSalt(salt);
        }
        this.updateById(sysUser);

        //检查角色是否越权
        checkRole(sysUser);

        //保存用户与角色关系
        sysUserRoleService.saveOrUpdate(db.getUserId(), sysUser.getRoleIdList());
    }

    @Override
    public void deleteBatch(Long[] userId) {
        this.removeByIds(Arrays.asList(userId));
    }

    @Override
    public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUser userEntity = new SysUser();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new QueryWrapper<SysUser>().eq("user_id", userId).eq("password", password));
    }

    /**
     * 检查角色是否越权
     */
    private void checkRole(SysUser user) {
        if (user.getRoleIdList() == null || user.getRoleIdList().size() == 0) {
            return;
        }
        //如果不是超级管理员，则需要判断用户的角色是否自己创建
        if (user.getCreateUserId() == Constant.SUPER_ADMIN) {
            return;
        }

        //查询用户创建的角色列表
        List<Long> roleIdList = sysRoleService.queryRoleIdList(user.getCreateUserId());

        //判断是否越权
        if (!roleIdList.containsAll(user.getRoleIdList())) {
            throw CommonException.FAILURE("新增用户所选角色，不是本人创建");
        }
    }
}

package com.lj.iot.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.system.dto.SysUserAddDto;
import com.lj.iot.common.system.dto.SysUserEditDto;
import com.lj.iot.common.system.entity.SysUser;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface ISysUserService extends IService<SysUser> {

    List<String> queryAllPerms(Long userId);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);

    Set<String> getUserPermissions(long userId);

    /**
     * 根据用户名，查询系统用户
     */
    SysUser queryByUserName(String username);

    /**
     * 保存用户
     */
    void saveUser(SysUserAddDto user, Long userId);

    /**
     * 修改用户
     */
    void update(SysUserEditDto user, Long userId);

    /**
     * 删除用户
     */
    void deleteBatch(Long[] userIds);

    /**
     * 修改密码
     * @param userId       用户ID
     * @param password     原密码
     * @param newPassword  新密码
     */
    boolean updatePassword(Long userId, String password, String newPassword);
}

package com.lj.iot.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.system.entity.SysRoleMenu;

import java.util.List;

/**
 * <p>
 * 角色与菜单对应关系 服务类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface ISysRoleMenuService extends IService<SysRoleMenu> {

    void saveOrUpdate(Long roleId, List<Long> menuIdList);

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(Long roleId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(Long[] roleIds);
}

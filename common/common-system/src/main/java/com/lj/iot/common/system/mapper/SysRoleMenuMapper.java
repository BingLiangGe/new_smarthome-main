package com.lj.iot.common.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色与菜单对应关系 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 根据角色ID，获取菜单ID列表
     */
    List<Long> queryMenuIdList(@Param("roleId") Long roleId);

    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(@Param("roleIds") Long[] roleIds);
}

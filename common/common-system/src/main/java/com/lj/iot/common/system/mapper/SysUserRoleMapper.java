package com.lj.iot.common.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户与角色对应关系 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(@Param("userId") Long userId);


    /**
     * 根据角色ID数组，批量删除
     */
    int deleteBatch(@Param("roleIds") Long[] roleIds);
}

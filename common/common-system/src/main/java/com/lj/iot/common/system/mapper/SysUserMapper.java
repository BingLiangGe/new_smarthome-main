package com.lj.iot.common.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询用户的所有权限
     * @param userId  用户ID
     */
    List<String> queryAllPerms(@Param("userId") Long userId);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(@Param("userId") Long userId);

    /**
     * 根据用户名，查询系统用户
     */
    SysUser queryByUserName2(@Param("username") String username);
}

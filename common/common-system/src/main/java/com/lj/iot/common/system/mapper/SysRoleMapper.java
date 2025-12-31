package com.lj.iot.common.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.common.system.entity.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {


    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(@Param("createUserId") Long createUserId);
}

package com.lj.iot.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.system.dto.SysRoleAddDto;
import com.lj.iot.common.system.dto.SysRoleEditDto;
import com.lj.iot.common.system.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author xm
 * @since 2022-10-10
 */
public interface ISysRoleService extends IService<SysRole> {


    void saveRole(SysRoleAddDto dto ,Long userId);

    void update(SysRoleEditDto dto,Long userId);

    void deleteBatch(Long[] roleIds);


    /**
     * 查询用户创建的角色ID列表
     */
    List<Long> queryRoleIdList(Long createUserId);
}

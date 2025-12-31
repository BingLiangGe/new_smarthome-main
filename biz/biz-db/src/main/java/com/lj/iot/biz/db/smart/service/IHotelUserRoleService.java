package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.HotelUserRole;

import java.util.List;

/**
 * <p>
 * 酒店用户与角色对应关系 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelUserRoleService extends IService<HotelUserRole> {

    void saveOrUpdate(Long hotelId, String hotelUserId, Long roleId);

    /**
     * 根据用户ID，获取角色ID列表
     */
    List<Long> queryRoleIdList(String hotelUserId, Long hotelId);

    /**
     * 删除用户角色
     */
    void delete(Long hotelId, String hotelUserId);
}

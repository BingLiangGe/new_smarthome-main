package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.HotelRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店角色与菜单对应关系 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelRoleMenuService extends IService<HotelRoleMenu> {

    List<String> permissions(@Param("hotelId") Long hotelId, @Param("roleId") Long roleId);

    List<String> mainPermissions();

    void saveOrUpdate(Long hotelId, Long roleId, List<Long> menuIdList);
}

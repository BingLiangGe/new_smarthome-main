package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.HotelRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店角色与菜单对应关系 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelRoleMenuMapper extends BaseMapper<HotelRoleMenu> {

    List<String> permissions(@Param("hotelId") Long hotelId, @Param("roleId") Long roleId);

    List<String> mainPermissions();
}

package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lj.iot.biz.db.smart.entity.HotelMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店菜单管理 Mapper 接口
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface HotelMenuMapper extends BaseMapper<HotelMenu> {

    List<HotelMenu> mainNav();
    List<HotelMenu> roleNav(@Param("roleId") Long roleId);
    List<HotelMenu> mainMenu();
    List<HotelMenu> roleMenu(@Param("roleId") Long roleId);

    List<Long> queryMenuIdList(@Param("roleId") Long roleId);
}

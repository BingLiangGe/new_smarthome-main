package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.db.smart.entity.HotelMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒店菜单管理 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelMenuService extends IService<HotelMenu> {

    /**
     * 获取用户菜单列表
     */
    List<HotelMenu> nav(Boolean isMain, Long hotelId, String hotelUserId);

    List<HotelMenu> mainNav();

    List<HotelMenu> roleNav(Long roleId);

    List<HotelMenu> mainMenu();

    List<HotelMenu> allMenu();

    List<HotelMenu> roleMenu(Long roleId);

    List<Long> queryMenuIdList(@Param("roleId") Long roleId);

}

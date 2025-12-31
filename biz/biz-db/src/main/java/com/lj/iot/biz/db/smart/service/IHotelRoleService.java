package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HotelRoleAddDto;
import com.lj.iot.biz.base.dto.HotelRoleEditDto;
import com.lj.iot.biz.base.dto.IdDto;
import com.lj.iot.biz.db.smart.entity.HotelRole;
import com.lj.iot.common.base.dto.PageDto;
import com.lj.iot.common.base.vo.CommonResultVo;

import java.util.List;

/**
 * <p>
 * 酒店角色 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelRoleService extends IService<HotelRole> {

    public CommonResultVo<HotelRole> addParent(HotelRoleAddDto dto, Boolean isMain, Long hotelId, String hotelUserId);

    void checkPerms(List<Long> menuIdList, Boolean isMain, Long hotelId, String hotelUserId);

    void checkPerms(Long roleId, Boolean isMain, Long hotelId, String hotelUserId);

    HotelRole add(HotelRoleAddDto dto, Boolean isMain, Long hotelId, String hotelUserId);

    HotelRole edit(HotelRoleEditDto dto, Boolean isMain, Long hotelId, String hotelUserId);

    void delete(IdDto dto, Long hotelId, String hotelUserId);

    IPage<HotelRole> customPage(PageDto pageDto, Long hotelId);

}

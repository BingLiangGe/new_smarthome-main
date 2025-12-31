package com.lj.iot.biz.db.smart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.biz.base.dto.HotelUserAccountAddDto;
import com.lj.iot.biz.base.dto.HotelUserAccountEditDto;
import com.lj.iot.biz.base.dto.IdStrDto;
import com.lj.iot.biz.base.dto.UserIdDto;
import com.lj.iot.biz.db.smart.entity.Hotel;
import com.lj.iot.biz.db.smart.entity.HotelUserAccount;
import com.lj.iot.common.util.util.PageUtil;

import java.util.List;

/**
 * <p>
 * 酒店用户账号表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelUserAccountService extends IService<HotelUserAccount> {


    /**
     * 获取酒店分页
     * @param pageIndex
     * @param pageSize
     * @param hotel
     * @return
     */
    public PageUtil<Hotel> getHotelLimit(Integer pageIndex, Integer pageSize, Hotel hotel);


    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param userAccount
     * @return
     */
    public PageUtil<HotelUserAccount> getHotelUserLimit(Integer pageIndex, Integer pageSize, HotelUserAccount userAccount);

    /**
     * 新增
     *
     * @param hotelId
     * @param actualUserId
     * @param dto
     * @return
     */
    HotelUserAccount add(Boolean isMain, Long hotelId, String hotelUserId,String actualUserId, HotelUserAccountAddDto dto);

    /**
     * 编辑
     *
     * @param hotelId
     * @param actualUserId
     * @param dto
     * @return
     */
    HotelUserAccount edit(Boolean isMain, Long hotelId,String userId,String actualUserId, HotelUserAccountEditDto dto);

    /**
     * 删除
     *
     * @param hotelId
     * @param actualUserId
     * @param dto
     */
    HotelUserAccount delete(Long hotelId, String actualUserId, UserIdDto dto);
}

package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.Hotel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lj.iot.common.util.util.PageUtil;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 酒店 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelService extends IService<Hotel> {

    List<Map>  getHomeByInfo(Integer hotelId, Integer floorId, String homeName);

    PageUtil<Hotel> findHotelPage(Integer pageIndex,Integer pageSize,Hotel hotel);
    List<Hotel>  customList(String userId);
}

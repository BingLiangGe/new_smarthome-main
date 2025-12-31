package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.Hotel;
import com.lj.iot.biz.db.smart.mapper.HotelMapper;
import com.lj.iot.biz.db.smart.service.IHotelService;
import com.lj.iot.common.util.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 酒店 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Resource
    private HotelMapper hotelMapper;

    @Override
    public List<Map> getHomeByInfo(Integer hotelId, Integer floorId, String homeName) {
        return hotelMapper.getHomeByInfo(hotelId,floorId,homeName);
    }

    @Override
    public PageUtil<Hotel> findHotelPage(Integer pageIndex, Integer pageSize, Hotel hotel) {
        PageUtil<Hotel> page = new PageUtil<Hotel>();

        page.setRows(hotelMapper.findHotelPageLimit(pageIndex, pageSize, hotel));
        page.setTotal(hotelMapper.findHotelPageLimitCount(hotel));

        return page;
    }

    @Override
    public List<Hotel> customList(String userId) {
        return this.baseMapper.customList(userId);
    }
}

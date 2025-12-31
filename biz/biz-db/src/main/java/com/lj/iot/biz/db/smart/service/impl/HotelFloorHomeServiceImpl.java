package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.lj.iot.biz.db.smart.mapper.HotelFloorHomeMapper;
import com.lj.iot.biz.db.smart.service.IHotelFloorHomeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 楼层-房间关系表 服务实现类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
@DS("smart")
@Service
public class HotelFloorHomeServiceImpl extends ServiceImpl<HotelFloorHomeMapper, HotelFloorHome> implements IHotelFloorHomeService {

    @Override
    public List<FloorHomeVo> listFloorHomeVo(Long hotelId, String userId) {
        return this.baseMapper.listFloorHomeVo(hotelId, userId);
    }

    @Override
    public List<FloorHomeVo> listFloorHomeByFloorId(Long floorId, String userId) {
        return this.baseMapper.listFloorHomeByFloorId(floorId, userId);
    }

}

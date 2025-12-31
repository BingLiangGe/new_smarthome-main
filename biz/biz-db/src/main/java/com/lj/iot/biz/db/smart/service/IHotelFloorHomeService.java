package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.FloorHomeVo;
import com.lj.iot.biz.db.smart.entity.HotelFloorHome;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 楼层-房间关系表 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelFloorHomeService extends IService<HotelFloorHome> {

    List<FloorHomeVo> listFloorHomeVo(Long hotelId, String userId);

    List<FloorHomeVo> listFloorHomeByFloorId(Long FloorId,String userId);

}

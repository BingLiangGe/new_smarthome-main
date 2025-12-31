package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.base.vo.HomeRoomListVo;
import com.lj.iot.biz.base.vo.HotelFloorRoomVo;
import com.lj.iot.biz.db.smart.entity.HotelFloor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 楼层 服务类
 * </p>
 *
 * @author xm
 * @since 2022-12-02
 */
public interface IHotelFloorService extends IService<HotelFloor> {

    /**
     * 获取酒店楼层房间
     * @param dto
     * @return
     */
    List<HotelFloorRoomVo> getHotelFloorRoomList(HotelFloorRoomVo dto);

    /**
     * 查询iot酒店房间
     * @param hotelId
     * @param roomId
     * @return
     */
    HotelFloorRoomVo getHotelFloorRoom(Long roomId, Long hotelId);
}

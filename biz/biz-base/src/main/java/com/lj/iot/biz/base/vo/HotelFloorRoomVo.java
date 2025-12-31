package com.lj.iot.biz.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 酒店楼层房间信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelFloorRoomVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long hotelId;

    /**
     * 酒店房间id
     */
    private Long roomId;

    /**
     * 酒店名称
     */
    private String hotelName;

    /**
     * 楼层id
     */
    private Long floorId;

    /**
     * 楼层名
     */
    private String floorName;

    /**
     * 酒店名称
     */
    private String roomName;

    /**
     * iot用户id
     */
    private String iotHotelUserId;
}

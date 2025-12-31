package com.lj.iot.biz.db.smart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeRoomDto;
import com.lj.iot.biz.base.dto.HomeRoomPageDto;
import com.lj.iot.biz.db.smart.entity.HomeRoom;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 用户房间表 Mapper 接口
 *
 * @author xm
 * @since 2022-07-13
 */
public interface HomeRoomMapper extends BaseMapper<HomeRoom> {

    IPage<HomeRoom> customPage(IPage<HomeRoom> page, @Param("params") HomeRoomPageDto pageDto);

    /**
     * 根据homeId和HomeName查询
     *
     * @param homeId
     * @param roomName
     * @return
     */
    Set<Long> setIdByHomeIdAndHomeName(@Param("homeId") Long homeId, @Param("roomName") String roomName);

    /**
     * 查询家房间是否存在
     *
     * @param homeRoomDto
     * @return
     */
    Boolean countHomeRoomByCondition(@Param("params") HomeRoomDto homeRoomDto);

    HomeRoom first(@Param("homeId") Long homeId, @Param("userId") String userId);

    List<HomeRoom> customList(@Param("homeId") Long homeId);

    List<HomeRoom> customListByHomeIdAndUserId(@Param("homeId") Long homeId, @Param("userId") String userId);

    List<HomeRoom> findListByHomeIdAndRoomId(@Param("homeId")  Long homeId, @Param("roomId")  Long roomId);
}

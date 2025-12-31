package com.lj.iot.biz.db.smart.mapper;

import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * 音乐表 Mapper 接口
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface MusicMenuMapper extends BaseMapper<MusicMenu> {

    List<MusicMenu> findUserid(@Param("uId")String uId);


    void removeAll(@Param("deviceId") String deviceId);

    MusicMenu selectByMusicMenu(@Param("musicId")String musicId,@Param("deviceId") String deviceId);
}

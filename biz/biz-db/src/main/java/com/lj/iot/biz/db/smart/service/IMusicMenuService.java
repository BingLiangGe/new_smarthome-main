package com.lj.iot.biz.db.smart.service;

import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * 音乐表 服务类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
public interface IMusicMenuService extends IService<MusicMenu> {


    List<MusicMenu> findUserid(String uId);

    void removeAll(String deviceId);

    MusicMenu selectByMusicMenu(String musicId, String deviceId);
}


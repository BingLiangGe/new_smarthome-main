package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.injector.methods.SelectList;
import com.lj.iot.biz.db.smart.entity.MusicMenu;
import com.lj.iot.biz.db.smart.mapper.MusicMenuMapper;
import com.lj.iot.biz.db.smart.service.IMusicMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * 音乐表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class MusicMenuServiceImpl extends ServiceImpl<MusicMenuMapper, MusicMenu> implements IMusicMenuService {


    @Override
    public List<MusicMenu> findUserid(String uId) {
        return baseMapper.findUserid(uId);
    }

    @Override
    public void removeAll(String deviceId) {
        baseMapper.removeAll(deviceId);
    }

    @Override
    public MusicMenu selectByMusicMenu(String musicId, String deviceId) {
        return baseMapper.selectByMusicMenu(musicId,deviceId);
    }
}

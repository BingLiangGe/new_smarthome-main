package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.biz.db.smart.entity.ScenePublicIcon;
import com.lj.iot.biz.db.smart.mapper.ScenePublicIconMapper;
import com.lj.iot.biz.db.smart.service.IScenePublicIconService;
import org.springframework.stereotype.Service;

@DS("smart")
@Service
public class ScenePublicIconServiceImpl extends ServiceImpl<ScenePublicIconMapper, ScenePublicIcon> implements IScenePublicIconService {
}

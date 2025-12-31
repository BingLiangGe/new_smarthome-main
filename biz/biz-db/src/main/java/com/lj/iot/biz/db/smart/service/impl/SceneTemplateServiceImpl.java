package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.SceneTemplate;
import com.lj.iot.biz.db.smart.mapper.SceneTemplateMapper;
import com.lj.iot.biz.db.smart.service.ISceneTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 情景模板 服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-03-08
 */
@DS("smart")
@Service
public class SceneTemplateServiceImpl extends ServiceImpl<SceneTemplateMapper, SceneTemplate> implements ISceneTemplateService {

}

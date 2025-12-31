package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.MeshKeyConfig;
import com.lj.iot.biz.db.smart.mapper.MeshKeyConfigMapper;
import com.lj.iot.biz.db.smart.service.IMeshKeyConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * Mesh设备按键配置 服务实现类
 * 
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class MeshKeyConfigServiceImpl extends ServiceImpl<MeshKeyConfigMapper, MeshKeyConfig> implements IMeshKeyConfigService {

}

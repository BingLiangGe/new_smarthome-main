package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.PublicFile;
import com.lj.iot.biz.db.smart.mapper.PublicFileMapper;
import com.lj.iot.biz.db.smart.service.IPublicFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 产品介绍视频链接表 服务实现类
 *
 *
 * @author xm
 * @since 2022-07-13
 */
@DS("smart")
@Service
public class PublicFileServiceImpl extends ServiceImpl<PublicFileMapper, PublicFile> implements IPublicFileService {

}

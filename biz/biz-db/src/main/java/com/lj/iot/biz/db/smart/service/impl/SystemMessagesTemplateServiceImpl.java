package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.SystemMessagesTemplate;
import com.lj.iot.biz.db.smart.mapper.SystemMessagesTemplateMapper;
import com.lj.iot.biz.db.smart.service.ISystemMessagesTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-03-22
 */
@DS("smart")
@Service
public class SystemMessagesTemplateServiceImpl extends ServiceImpl<SystemMessagesTemplateMapper, SystemMessagesTemplate> implements ISystemMessagesTemplateService {

}

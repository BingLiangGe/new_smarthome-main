package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lj.iot.biz.base.dto.HomeUserPageDto;
import com.lj.iot.biz.db.smart.entity.SosContact;
import com.lj.iot.biz.db.smart.entity.SystemMessages;
import com.lj.iot.biz.db.smart.mapper.SystemMessagesMapper;
import com.lj.iot.biz.db.smart.service.ISystemMessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lj.iot.common.util.PageUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xm
 * @since 2023-02-24
 */
@DS("smart")
@Service
public class SystemMessagesServiceImpl extends ServiceImpl<SystemMessagesMapper, SystemMessages> implements ISystemMessagesService {


}

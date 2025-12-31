package com.lj.iot.biz.db.smart.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.lj.iot.biz.db.smart.entity.UserDeviceNode;
import com.lj.iot.biz.db.smart.mapper.UserDeviceNodeMapper;
import com.lj.iot.biz.db.smart.service.IUserDeviceNodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tyj
 * @since 2024-02-19
 */
@DS("smart")
@Service
public class UserDeviceNodeServiceImpl extends ServiceImpl<UserDeviceNodeMapper, UserDeviceNode> implements IUserDeviceNodeService {

}
